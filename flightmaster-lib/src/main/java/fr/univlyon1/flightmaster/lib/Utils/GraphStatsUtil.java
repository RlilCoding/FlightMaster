package fr.univlyon1.flightmaster.lib.Utils;

import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.graph.Graph;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Node;

import static org.graphstream.algorithm.Toolkit.unweightedEccentricity;

/**
 * Utility class for computing various statistics and properties of a graph.
 */
public class GraphStatsUtil {

    /**
     * Computes the number of connected components in the given graph.
     *
     * @param graph The graph for which to compute the connected components count.
     * @return The number of connected components in the graph.
     */
    public static int getConnectComponentsCount(Graph graph) {
        ConnectedComponents cc = new ConnectedComponents();
        cc.init(graph);
        cc.compute();
        return cc.getConnectedComponentsCount();
    }

    /**
     * Computes the average degree of nodes in the given graph.
     *
     * @param graph The graph for which to compute the average degree.
     * @return The average degree of nodes in the graph.
     */
    public static double getAverageDegree(Graph graph) {
        return Toolkit.averageDegree(graph);
    }

    /**
     * Computes the diameter of the given graph.
     *
     * @param graph The graph for which to compute the diameter.
     * @return The diameter of the graph, or null if infinite.
     */
    public static Double diameter(Graph graph) {
        double maxEccentricity = 0;

        for (Node node : graph) {
            int ecc = unweightedEccentricity(node, false);  // false for undirected
            if (ecc == 0 && graph.getNodeCount() > 1) {  // Check if the graph has more than one node
                return null;
            }
            maxEccentricity = Math.max(maxEccentricity, ecc);
        }
        return maxEccentricity;
    }

}
