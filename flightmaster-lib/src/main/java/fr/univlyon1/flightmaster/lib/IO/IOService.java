package fr.univlyon1.flightmaster.lib.IO;

import fr.univlyon1.flightmaster.lib.Exception.CSVParsingException;
import fr.univlyon1.flightmaster.lib.Exception.GraphExportException;
import fr.univlyon1.flightmaster.lib.Exception.GraphParsingException;
import fr.univlyon1.flightmaster.lib.Exception.StatsExportException;
import fr.univlyon1.flightmaster.lib.Models.Airport;
import fr.univlyon1.flightmaster.lib.Models.Flight;
import fr.univlyon1.flightmaster.lib.Models.Point;
import fr.univlyon1.flightmaster.lib.Utils.GeoConverterUtil;
import fr.univlyon1.flightmaster.lib.Utils.GraphStatsUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to handle File Loading and Saving operations.
 * Provides methods to load airports, flights, and graphs from CSV/text files,
 * and export graph coloring and statistics.
 */
public class IOService {

    /**
     * Loads airports from a CSV file and returns a map of airports mapped by their code.
     *
     * @param path Path to the CSV file containing airport data.
     * @return Map of airports keyed by their airport code.
     * @throws CSVParsingException If there is an error parsing the CSV file.
     */
    public static Map<String, Airport> loadAirports(String path) throws CSVParsingException {
        Map<String, Airport> airports = new HashMap<>();
        try (Reader reader = new FileReader(path);
             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                     .setDelimiter(';')
                     .setHeader("Code", "City", "LatDeg", "LatMin", "LatSec", "LatDir", "LonDeg", "LonMin", "LonSec", "LonDir")
                     .setSkipHeaderRecord(false)
                     .build())) {
            for (CSVRecord record : parser) {
                if (record.size() != 10) {
                    throw new CSVParsingException("Invalid number of columns in Airport CSV file");
                }
                String code = record.get("Code");
                String city = record.get("City");
                String latDeg = record.get("LatDeg");
                String latMin = record.get("LatMin");
                String latSec = record.get("LatSec");
                String latDir = record.get("LatDir");
                String lonDeg = record.get("LonDeg");
                String lonMin = record.get("LonMin");
                String lonSec = record.get("LonSec");
                String lonDir = record.get("LonDir");
                if (code == null || code.isEmpty() || city == null || city.isEmpty()
                        || latDeg == null || latDeg.isEmpty() || latMin == null || latMin.isEmpty()
                        || latSec == null || latSec.isEmpty() || latDir == null || latDir.isEmpty()
                        || lonDeg == null || lonDeg.isEmpty() || lonMin == null || lonMin.isEmpty()
                        || lonSec == null || lonSec.isEmpty() || lonDir == null || lonDir.isEmpty()) {
                    continue;
                }

                double latitude = GeoConverterUtil.convertDMSToDecimal(latDeg, latMin, latSec, latDir);
                double longitude = GeoConverterUtil.convertDMSToDecimal(lonDeg, lonMin, lonSec, lonDir);
                Point position = GeoConverterUtil.convertGeoToCartesian(latitude, longitude);

                Airport airport = new Airport(city, position);
                airport.setLatitude(latitude);
                airport.setLongitude(longitude);
                airports.put(code, airport);
            }
        } catch (Exception e) {
            throw new CSVParsingException("Error while parsing Airport CSV file", e);
        }
        return airports;
    }

    /**
     * Loads flights from a CSV file and returns a list of Flight objects.
     *
     * @param path Path to the CSV file containing flight data.
     * @return List of Flight objects loaded from the CSV file.
     * @throws CSVParsingException If there is an error parsing the CSV file.
     */
    public static List<Flight> loadFlights(String path) throws CSVParsingException {
        List<Flight> flights = new ArrayList<>();
        try (Reader reader = new FileReader(path);
             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                     .setDelimiter(';')
                     .setHeader("Code", "DepartureAirport", "ArrivalAirport", "DepartureTimeHours", "DepartureTimeMinutes", "FlightLength")
                     .setSkipHeaderRecord(false)
                     .build())) {
            for (CSVRecord record : parser) {
                if (record.size() != 6) {
                    throw new CSVParsingException("Invalid number of columns in Flight CSV file");
                }
                String code = record.get("Code");
                String departureAirport = record.get("DepartureAirport");
                String arrivalAirport = record.get("ArrivalAirport");
                String departureTimeHours = record.get("DepartureTimeHours");
                String departureTimeMinutes = record.get("DepartureTimeMinutes");
                String flightLength = record.get("FlightLength");

                if (code == null || code.isEmpty() || departureAirport == null || departureAirport.isEmpty()
                        || arrivalAirport == null || arrivalAirport.isEmpty()
                        || departureTimeHours == null || departureTimeHours.isEmpty()
                        || departureTimeMinutes == null || departureTimeMinutes.isEmpty()
                        || flightLength == null || flightLength.isEmpty()) {
                    continue;
                }

                try {
                    int hours = Integer.parseInt(departureTimeHours);
                    int minutes = Integer.parseInt(departureTimeMinutes);
                    int length = Integer.parseInt(flightLength);
                    LocalTime departureTime = LocalTime.of(hours, minutes);
                    Duration duration = Duration.ofMinutes(length);
                    flights.add(new Flight(code, departureAirport, arrivalAirport, departureTime, duration));
                } catch (Exception e) {
                    System.out.println("Error while parsing flight " + code + " : " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new CSVParsingException("Error while parsing flight CSV file", e);
        }
        return flights;
    }

    /**
     * Loads a graph from a text file and returns a Graph object.
     *
     * @param pathStr Path to the text file containing graph data.
     * @return Graph object loaded from the text file.
     * @throws GraphParsingException If there is an error parsing the graph file.
     */
    public static Graph loadGraph(String pathStr) throws GraphParsingException {
        Graph graph;
        try {
            Path path = Paths.get(pathStr);
            graph = new MultiGraph(path.getFileName().toString());
            graph.setAttribute("ui.antialias");
            graph.setAttribute("ui.quality");
            List<String> allLines = Files.readAllLines(path);

            for (int i = 0; i < allLines.size(); i++) {
                if (i == 0) {
                    graph.setAttribute("kmax", Integer.parseInt(allLines.get(i)));
                }else if (i == 1){
                    Integer.parseInt(allLines.get(i));
                } if (i > 1) {
                    String[] edge = allLines.get(i).split(" ");
                    if (edge.length != 2)
                        throw new GraphParsingException("Invalid number of columns in Graph file");

                    if (graph.getNode(edge[0]) == null) {
                        graph.addNode(edge[0]);
                        graph.getNode(edge[0]).setAttribute("ui.label", edge[0]);
                        graph.getNode(edge[0]).setAttribute("ui.style", "text-size: 1; text-color: #808080;");
                    }

                    if (graph.getNode(edge[1]) == null) {
                        graph.addNode(edge[1]);
                        graph.getNode(edge[1]).setAttribute("ui.label", edge[1]);
                        graph.getNode(edge[1]).setAttribute("ui.style", "text-size: 1; text-color: #808080;");
                    }

                    graph.addEdge(edge[0] + "-" + edge[1], edge[0], edge[1]);
                }
            }
        } catch (Exception e) {
            throw new GraphParsingException("Error while parsing graph file", e);
        }
        return graph;
    }

    /**
     * Exports the coloring of a graph to a text file.
     *
     * @param graph   Graph to export the coloring from.
     * @param pathStr Path to the output text file.
     * @throws GraphExportException If there is an error exporting the graph coloring.
     */
    public static void exportGraphColoring(Graph graph, String pathStr) throws GraphExportException {
        Path path = Paths.get(pathStr);
        try (BufferedWriter writer = Files.newBufferedWriter(path,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            for (Node node : graph) {
                writer.write(node.getId() + "; " + node.getAttribute("color"));
                writer.newLine();
            }
            writer.flush();
        } catch (Exception e) {
            throw new GraphExportException("Error while exporting graph file", e);
        }
    }

    /**
     * Exports conflicts between filenames and their corresponding conflicts count to a CSV file.
     *
     * @param filenames List of filenames.
     * @param conflicts List of conflicts counts.
     * @param pathStr   Path to the output CSV file.
     * @throws GraphExportException If there is an error exporting the conflicts file.
     */
    public static void exportConflicts(List<String> filenames, List<Integer> conflicts, String pathStr) throws GraphExportException {
        Path path = Paths.get(pathStr);
        try (BufferedWriter writer = Files.newBufferedWriter(path,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            for (int i = 0; i < filenames.size(); i++) {
                writer.write(filenames.get(i) + " ; " + conflicts.get(i));
                writer.newLine();
            }
            writer.flush();
        } catch (Exception e) {
            throw new GraphExportException("Error while exporting conflicts file", e);
        }
    }

    /**
     * Exports statistics of a graph to a CSV file.
     *
     * @param graph         Graph to export statistics from.
     * @param outputPathStr Path to the output CSV file.
     * @throws StatsExportException If there is an error exporting the statistics file.
     */
    public static void exportStats(Graph graph, String outputPathStr) throws StatsExportException {
        Path path = Paths.get(outputPathStr);
        try (BufferedWriter writer = Files.newBufferedWriter(path,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            writer.write("nomduFichier;noeuds;arret;degres;nbComposantes;diametre;nbConflits");
            writer.newLine();
            writer.write(graph.getAttribute("fileName") + ";" + graph.getNodeCount() + ";" + graph.getEdgeCount() + ";" + GraphStatsUtil.getAverageDegree(graph) + ";" + GraphStatsUtil.getConnectComponentsCount(graph) + ";" + GraphStatsUtil.diameter(graph) + ";" + graph.getAttribute("conflictsCount"));
            writer.flush();
        } catch (Exception e) {
            throw new StatsExportException("Error while exporting statistics file", e);
        }
    }
}
