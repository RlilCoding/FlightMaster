package fr.univlyon1.flightmaster.lib;

import fr.univlyon1.flightmaster.lib.Algos.Coloring;
import fr.univlyon1.flightmaster.lib.Exception.GraphParsingException;
import fr.univlyon1.flightmaster.lib.IO.IOService;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.junit.jupiter.api.Test;


import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class ColoringTests {

    @Test
    public void testWelshPowell() throws GraphParsingException {
        Graph graph = IOService.loadGraph("TestFiles/graph-test0.txt");
        int conflicts = Coloring.welshPowell(graph);
        assertEquals(0, conflicts, "The number of conflicts should be 0.");
        assertNumberOfColors(graph);

        Graph graph2 = IOService.loadGraph("TestFiles/graph-test1.txt");
        int conflicts2 = Coloring.welshPowell(graph2);
        assertEquals(4, conflicts2, "The number of conflicts should be 4.");
        assertNumberOfColors(graph2);
    }

    @Test
    public void testDSATURColoring() throws GraphParsingException {
        Graph graph = IOService.loadGraph("TestFiles/graph-test0.txt");
        int conflicts = Coloring.DSATUR(graph);
        assertEquals(0, conflicts, "The number of conflicts should be 0.");
        assertNumberOfColors(graph);

        Graph graph2 = IOService.loadGraph("TestFiles/graph-test1.txt");
        int conflicts2 = Coloring.DSATUR(graph2);
        assertEquals(4, conflicts2, "The number of conflicts should be 4.");
        assertNumberOfColors(graph2);
    }

    @Test
    public void testRLF() throws GraphParsingException {
        Graph graph = IOService.loadGraph("TestFiles/graph-test0.txt");
        int conflicts = Coloring.recursiveLargestFirst(graph);
        assertEquals(0, conflicts, "The number of conflicts should be 0.");
        assertNumberOfColors(graph);

        Graph graph2 = IOService.loadGraph("TestFiles/graph-test1.txt");
        int conflicts2 = Coloring.recursiveLargestFirst(graph2);
        assertEquals(5, conflicts2, "The number of conflicts should be 5.");
        assertNumberOfColors(graph2);
    }



    private void assertNumberOfColors(Graph graph) {
        HashSet<Integer> usedColors = new HashSet<>();
        for (Node node : graph) {
            usedColors.add((Integer) node.getAttribute("color"));
        }
        assertTrue(usedColors.size() <= (int)graph.getAttribute("kmax"), "The number of used colors should not exceed kmax");
    }

}
