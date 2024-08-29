package fr.univlyon1.flightmaster.lib;

import fr.univlyon1.flightmaster.lib.Algos.Coloring;
import fr.univlyon1.flightmaster.lib.Exception.CSVParsingException;
import fr.univlyon1.flightmaster.lib.Exception.GraphExportException;
import fr.univlyon1.flightmaster.lib.Exception.GraphParsingException;
import fr.univlyon1.flightmaster.lib.IO.IOService;
import fr.univlyon1.flightmaster.lib.Models.Airport;
import fr.univlyon1.flightmaster.lib.Models.Flight;
import org.graphstream.graph.Graph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class IOServiceTests {

    @Test
    public void testLoadAirports() throws CSVParsingException {
        Map<String, Airport> airports = IOService.loadAirports("./TestFiles/aeroports.txt");
        assertNotNull(airports, "The returned map should not be null.");
        assertFalse(airports.isEmpty(), "The map should not be empty.");

        //        MRS 43°26’8’‘N=43.4355556°, 5°12’49’‘E=5.21361111°, soit (420.387068;4607.15017)
        //        BES 48°26’52’‘N=48.4477778°, 4°25’6’‘O=4.41833333°, soit (-325.555103;4213.34092)
        //        LYS 45°43’35’‘N=45.7263889°, 5°5’27’‘E=5.09083333°, soit (394.648936;4429.95907)
        //        BOD 44°49’42’‘N=44.8283333°, 0°42’56’'O=0.715555556°, soit (-56.4286114;4518.10228)
        Airport MRS = airports.get("MRS");
        Airport BES = airports.get("BES");
        Airport LYS = airports.get("LYS");
        Airport BOD = airports.get("BOD");

        assertNotNull(MRS, "The airport MRS should be in the map.");
        assertNotNull(BES, "The airport BES should be in the map.");
        assertNotNull(LYS, "The airport LYS should be in the map.");
        assertNotNull(BOD, "The airport BOD should be in the map.");

        assertEquals("Marseille", MRS.getNom(), "The name of the airport should be correct.");
        assertEquals("Brest", BES.getNom(), "The name of the airport should be correct.");
        assertEquals("Lyon", LYS.getNom(), "The name of the airport should be correct.");
        assertEquals("Bordeaux", BOD.getNom(), "The name of the airport should be correct.");

        // asserting positions
        assertEquals(420.387068, MRS.getPosition().x, 0.00001, "The x coordinate should be correct.");
        assertEquals(4607.15017, MRS.getPosition().y, 0.00001, "The y coordinate should be correct.");

        assertEquals(-325.555103, BES.getPosition().x, 0.00001, "The x coordinate should be correct.");
        assertEquals(4213.34092, BES.getPosition().y, 0.00001, "The y coordinate should be correct.");

        assertEquals(394.648936, LYS.getPosition().x, 0.00001, "The x coordinate should be correct.");
        assertEquals(4429.95907, LYS.getPosition().y, 0.00001, "The y coordinate should be correct.");

        assertEquals(-56.4286114, BOD.getPosition().x, 0.00001, "The x coordinate should be correct.");
        assertEquals(4518.10228, BOD.getPosition().y, 0.00001, "The y coordinate should be correct.");


        assertEquals(94, airports.size(), "The map size should be correct");


        // test of bad files :
        // flight file instead of airport file
        assertThrows(CSVParsingException.class, () -> {
            IOService.loadAirports("./TestFiles/vol-test0.csv");
        });

        // with non-existing file
        assertThrows(CSVParsingException.class, () -> {
            IOService.loadAirports("./TestFiles/codedequalitélesang.java");
        });
    }

    @Test
    public void testLoadFlights() throws CSVParsingException {
        List<Flight> flights = IOService.loadFlights("./TestFiles/vol-test0.csv");
        assertNotNull(flights, "The returned list should not be null.");
        assertFalse(flights.isEmpty(), "The list should not be empty.");

        Flight MrsBes = flights.get(0);
        Flight LysBod = flights.get(1);

        assertNotNull(MrsBes, "The flight MrsBes should be in the list.");
        assertNotNull(LysBod, "The flight LysBod should be in the list.");

        assertEquals("AF000090", MrsBes.getId(), "The id of the flight should be correct.");
        assertEquals("AF000132", LysBod.getId(), "The id of the flight should be correct.");

        assertEquals("MRS", MrsBes.getDepartureAirport(), "The departure airport should be correct.");
        assertEquals("LYS", LysBod.getDepartureAirport(), "The departure airport should be correct.");

        assertEquals("BES", MrsBes.getArrivalAirport(), "The arrival airport should be correct.");
        assertEquals("BOD", LysBod.getArrivalAirport(), "The arrival airport should be correct.");

        assertEquals(LocalTime.of(7, 33), MrsBes.getDepartureTime(), "The departure time should be correct.");
        assertEquals(LocalTime.of(7, 34), LysBod.getDepartureTime(), "The departure time should be correct.");

        assertEquals(Duration.ofMinutes(81), MrsBes.getDuration(), "The duration should be correct.");
        assertEquals(Duration.ofMinutes(47), LysBod.getDuration(), "The duration should be correct.");

        assertEquals(2, flights.size(), "The list size should be correct.");

        // test of bad files :
        // graph file instead of flight file
        assertThrows(CSVParsingException.class, () -> {
            IOService.loadFlights("./TestFiles/graph-test0.txt");
        });

        // with non-existing file
        assertThrows(CSVParsingException.class, () -> {
            IOService.loadFlights("./TestFiles/codedequalitélesang.java");
        });
    }

    @Test
    public void testLoadGraph() throws GraphParsingException {
        Graph graph = IOService.loadGraph("./TestFiles/graph-test0.txt");
        assertNotNull(graph, "The graph should not be null.");
        assertEquals(4, graph.getAttribute("kmax"), "The kmax attribute should be correct.");
        assertEquals(11, graph.getNodeCount(), "The graph should have 11 nodes.");

        // asserting edges
        assertTrue(graph.getNode("1").hasEdgeBetween("2"), "The edge should exist.");
        assertTrue(graph.getNode("1").hasEdgeBetween("4"), "The edge should exist.");
        assertTrue(graph.getNode("1").hasEdgeBetween("7"), "The edge should exist.");
        assertTrue(graph.getNode("1").hasEdgeBetween("9"), "The edge should exist.");
        assertTrue(graph.getNode("2").hasEdgeBetween("3"), "The edge should exist.");
        assertTrue(graph.getNode("2").hasEdgeBetween("6"), "The edge should exist.");
        assertTrue(graph.getNode("2").hasEdgeBetween("8"), "The edge should exist.");
        assertTrue(graph.getNode("3").hasEdgeBetween("5"), "The edge should exist.");
        assertTrue(graph.getNode("3").hasEdgeBetween("7"), "The edge should exist.");
        assertTrue(graph.getNode("3").hasEdgeBetween("10"), "The edge should exist.");
        assertTrue(graph.getNode("4").hasEdgeBetween("5"), "The edge should exist.");
        assertTrue(graph.getNode("4").hasEdgeBetween("6"), "The edge should exist.");
        assertTrue(graph.getNode("4").hasEdgeBetween("10"), "The edge should exist.");
        assertTrue(graph.getNode("5").hasEdgeBetween("8"), "The edge should exist.");
        assertTrue(graph.getNode("5").hasEdgeBetween("9"), "The edge should exist.");
        assertTrue(graph.getNode("6").hasEdgeBetween("11"), "The edge should exist.");
        assertTrue(graph.getNode("7").hasEdgeBetween("11"), "The edge should exist.");
        assertTrue(graph.getNode("8").hasEdgeBetween("11"), "The edge should exist.");
        assertTrue(graph.getNode("9").hasEdgeBetween("11"), "The edge should exist.");
        assertTrue(graph.getNode("10").hasEdgeBetween("11"), "The edge should exist.");

        // test of bad files :
        // flight file instead of graph file
        assertThrows(GraphParsingException.class, () -> {
            IOService.loadGraph("./TestFiles/vol-test0.csv");
        });

        // with non-existing file
        assertThrows(GraphParsingException.class, () -> {
            IOService.loadGraph("./TestFiles/codedequalitélesang.java");
        });
    }

    @Test
    public void testGraphColoringExport(@TempDir Path tempDir) throws GraphParsingException, IOException, GraphExportException {
        Graph graph = IOService.loadGraph("./TestFiles/graph-test0.txt");

        Path testFile = tempDir.resolve("colo-eval0.txt");

        Coloring.welshPowell(graph);

        IOService.exportGraphColoring(graph, testFile.toString());


        // Read the data back line by line and compare it to the expected data
        List<String> lines = Files.readAllLines(testFile, StandardCharsets.UTF_8);
        assertEquals(graph.getNodeCount(), lines.size(), "The number of lines should be correct.");
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split("; ");
            assertEquals(parts[0], graph.getNode(i).getId(), "The node id should be correct.");
            assertEquals(parts[1], graph.getNode(i).getAttribute("color").toString(), "The color should be correct.");
        }

        // exporting to an invalid path
        assertThrows(GraphExportException.class, () -> {
            IOService.exportGraphColoring(graph, "/path/to/nonexistent/directory/graph.txt");
        });

        assertThrows(GraphExportException.class, () -> {
            IOService.exportGraphColoring(graph, "");
        });
    }

    @Test
    public void testExportConflicts(@TempDir Path tempDir) throws GraphParsingException, IOException, GraphExportException {
        Graph graph = IOService.loadGraph("./TestFiles/graph-test0.txt");
        Graph graph2 = IOService.loadGraph("./TestFiles/graph-test1.txt");


        Path testFile = tempDir.resolve("coloration-groupe3.csv");

        List<String> filenames = new ArrayList<>();
        filenames.add("graph-test0.txt");
        filenames.add("graph-test1.txt");
        List<Integer> conflicts = new ArrayList<>();

        int numConflicts = Coloring.welshPowell(graph);
        conflicts.add(numConflicts);
        int numConflicts2 = Coloring.welshPowell(graph2);
        conflicts.add(numConflicts2);

        IOService.exportConflicts(filenames, conflicts, testFile.toString());


        // Read the data back line by line and compare it to the expected data
        List<String> lines = Files.readAllLines(testFile, StandardCharsets.UTF_8);
        assertEquals(2, lines.size(), "The number of lines should be correct.");
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(" ; ");
            assertEquals(filenames.get(i), parts[0], "The filename should be correct.");
            assertEquals(conflicts.get(i).toString(), parts[1], "The number of conflicts should be correct.");
        }

        // exporting to an invalid path
        assertThrows(GraphExportException.class, () -> {
            IOService.exportConflicts(filenames, conflicts, "/path/to/nonexistent/directory/graph.txt");
        });

        assertThrows(GraphExportException.class, () -> {
            IOService.exportConflicts(filenames, conflicts, "");
        });
    }
}
