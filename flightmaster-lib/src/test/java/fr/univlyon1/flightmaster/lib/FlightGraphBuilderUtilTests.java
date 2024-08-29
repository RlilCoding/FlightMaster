package fr.univlyon1.flightmaster.lib;

import fr.univlyon1.flightmaster.lib.Exception.CSVParsingException;
import fr.univlyon1.flightmaster.lib.IO.IOService;
import fr.univlyon1.flightmaster.lib.Models.Airport;
import fr.univlyon1.flightmaster.lib.Models.Flight;
import fr.univlyon1.flightmaster.lib.Utils.FlightGraphBuilderUtil;
import fr.univlyon1.flightmaster.lib.Utils.GraphStatsUtil;
import org.graphstream.graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FlightGraphBuilderUtilTests {
    @Test
    public void testBuildFlightGraph() throws CSVParsingException {
        List<Flight> flights = IOService.loadFlights("./TestFiles/vol-test2.csv");
        Map<String, Airport> airports = IOService.loadAirports("./TestFiles/aeroports.txt");

        Graph graph = FlightGraphBuilderUtil.buildFlightGraph(airports, flights);


        assertNotNull(graph, "The graph should not be null.");
        assertEquals(flights.size(), graph.getNodeCount(), "The number of nodes should be correct.");

        assertEquals(5, graph.getEdgeCount(), "The number of edges should be correct.");

        assertEquals(2.0, GraphStatsUtil.getAverageDegree(graph), "The average degree should be correct.");

        assertEquals(1, GraphStatsUtil.getConnectComponentsCount(graph), "The number of connected components should be correct.");

        assertEquals(3.0, GraphStatsUtil.diameter(graph), "The diameter should be correct.");

    }
}
