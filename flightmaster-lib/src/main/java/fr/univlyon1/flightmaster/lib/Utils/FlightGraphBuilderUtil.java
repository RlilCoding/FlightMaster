package fr.univlyon1.flightmaster.lib.Utils;

import fr.univlyon1.flightmaster.lib.Models.Airport;
import fr.univlyon1.flightmaster.lib.Models.Flight;
import fr.univlyon1.flightmaster.lib.Models.Point;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Utility class for building a flight graph based on airport positions and flight schedules.
 */
public class FlightGraphBuilderUtil {

    /**
     * Builds a flight graph with a default deadline of 15 minutes.
     *
     * @param airports Map of airport codes to Airport
     * @param flights  List of Flight objects representing flights to be included in the graph.
     * @return Graph representing the flight connections.
     */
    public static Graph buildFlightGraph(Map<String, Airport> airports, List<Flight> flights) {
        return buildFlightGraph(airports, flights, 15);
    }

    /**
     * Builds a flight graph with a specified deadline in minutes.
     *
     * @param airports Map of airport codes to Airport
     * @param flights  List of Flight objects representing flights to be included in the graph.
     * @param deadline Maximum allowed time difference in minutes between arrival times at an intersection.
     * @return Graph representing the flight connections.
     */
    public static Graph buildFlightGraph(Map<String, Airport> airports, List<Flight> flights, int deadline) {
        Graph graph = new MultiGraph("FlightGraph");
        graph.setAttribute("ui.antialias");
        graph.setAttribute("ui.quality");

        for (Flight flight : flights) {
            graph.addNode(flight.getId());
        }

        for (int i = 0; i < flights.size() - 1; i++) {
            for (int j = i + 1; j < flights.size(); j++) {
                Flight flight1 = flights.get(i);
                Flight flight2 = flights.get(j);
                Point intersectPos = CollisionUtil.findIntersection(
                        airports.get(flight1.getDepartureAirport()).getPosition(),
                        airports.get(flight1.getArrivalAirport()).getPosition(),
                        airports.get(flight2.getDepartureAirport()).getPosition(),
                        airports.get(flight2.getArrivalAirport()).getPosition()
                );

                if (intersectPos != null) {
                    LocalTime flight1EstimatedArrival = CollisionUtil.estimatedTimeOfArrivalAtIntersection(intersectPos, flight1, airports);
                    LocalTime flight2EstimatedArrival = CollisionUtil.estimatedTimeOfArrivalAtIntersection(intersectPos, flight2, airports);

                    Duration duration = Duration.between(flight1EstimatedArrival, flight2EstimatedArrival);

                    if (duration.abs().toMinutes() < deadline) {
                        graph.addEdge(flight1.getId() + "-" + flight2.getId(), flight1.getId(), flight2.getId());
                    }
                }
            }
        }

        return graph;
    }
}
