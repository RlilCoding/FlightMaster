package fr.univlyon1.flightmaster.lib.Algos;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Package containing the different coloring algorithms implementations used by flightmaster,
 * such as WelshPowell, DSATUR, and recursiveLargestFirst.
 */
public class Coloring {

    /**
     * Generates uniformly distributed color palette based on the maximum number of colors.
     *
     * @param kmax the maximum number of colors to generate
     * @return an array of color codes in hexadecimal format
     */
    private static String[] generateColorPalette(int kmax) {
        String[] colors = new String[kmax];
        for (int i = 0; i < kmax; i++) {
            float hue = (float) i / kmax;
            Color color = Color.getHSBColor(hue, 0.75f, 0.75f);
            colors[i] = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
        }
        return colors;
    }

    /**
     * Applies the Welsh-Powell graph coloring algorithm to the specified graph, with its default kmax value.
     * Colors nodes using the generated color palette and minimizes conflicts.
     * Equivalent to calling welshPowell(graph, (int) graph.getAttribute("kmax")).
     * @param graph the graph to be colored
     * @return the total number of conflicts after coloring
     */
    public static int welshPowell(Graph graph) {
        return welshPowell(graph, (int) graph.getAttribute("kmax"));
    }

    /**
     * Applies the Welsh-Powell graph coloring algorithm to the specified graph
     * with a custom maximum number of colors (kmax).
     *
     * @param graph the graph to be colored
     * @param kmax  the maximum number of colors to use
     * @return the total number of conflicts after coloring
     */
    public static int welshPowell(Graph graph, int kmax) {
        String[] colors = generateColorPalette(kmax);

        List<Node> nodes = Toolkit.degreeMap(graph);
        for (Node node : graph) {
            node.setAttribute("color", -1);
            node.setAttribute("ui.style", "fill-color: gray;");
        }

        // Attempt to color the nodes within the kmax limit
        for (int colorIndex = 1; colorIndex <= kmax; colorIndex++) {
            for (Node node : nodes) {
                if ((int) node.getAttribute("color") == -1) {
                    boolean isColorAvailable = true;
                    for (Node neighbor : node.neighborNodes().toList()) {
                        if ((int) neighbor.getAttribute("color") == colorIndex) {
                            isColorAvailable = false;
                            break;
                        }
                    }
                    if (isColorAvailable) {
                        node.setAttribute("color", colorIndex);
                        node.setAttribute("ui.style", "fill-color: " + colors[colorIndex - 1] + ";");
                    }
                }
            }
        }

        return colorizeUncoloredNodesWithMinConflicts(nodes, kmax, colors);
    }

    /**
     * Applies the DSATUR graph coloring algorithm to the specified graph.
     * Colors nodes using the generated color palette and minimizes conflicts.
     * Equivalent to calling DSATUR(graph, (int) graph.getAttribute("kmax")).
     * @param graph the graph to be colored
     * @return the total number of conflicts after coloring
     */
    public static int DSATUR(Graph graph) {
        return DSATUR(graph, (int) graph.getAttribute("kmax"));
    }

    /**
     * Calculates the saturation degree of a node based on its adjacent nodes.
     *
     * @param n the node for which to calculate the saturation degree
     * @return the saturation degree of the node
     */
    public static int getSaturationDegree(Node n) {
        Set<Integer> usedColors = new HashSet<>();
        for (Edge edge : n) {
            Node adjacent = edge.getOpposite(n);
            if ((int) adjacent.getAttribute("color") != -1) {
                usedColors.add((int) adjacent.getAttribute("color"));
            }
        }
        return usedColors.size();
    }

    /**
     * Applies the DSATUR graph coloring algorithm to the specified graph
     * with a custom maximum number of colors (kmax).
     *
     * @param graph the graph to be colored
     * @param kmax  the maximum number of colors to use
     * @return the total number of conflicts after coloring
     */
    public static int DSATUR(Graph graph, int kmax) {
        String[] colors = generateColorPalette(kmax);

        // Set all node colors to -1 (uncolored)
        for (Node node : graph) {
            node.setAttribute("color", -1);
        }

        PriorityQueue<Node> queue = new PriorityQueue<>((n1, n2) -> {
            int satDeg1 = getSaturationDegree(n1);
            int satDeg2 = getSaturationDegree(n2);
            if (satDeg1 != satDeg2) {
                return Integer.compare(satDeg2, satDeg1); // Higher saturation degree first
            }
            int deg1 = n1.getDegree();
            int deg2 = n2.getDegree();
            return Integer.compare(deg2, deg1); // Higher degree first otherwise
        });

        for (Node node : graph) {
            queue.add(node);
        }

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            Set<Integer> usedColors = new HashSet<>();
            for (Edge edge : node) {
                Node adjacent = edge.getOpposite(node);
                int color = (int) adjacent.getAttribute("color");
                if (color != -1) {
                    usedColors.add(color);
                }
            }

            // Find the first unused color that does not exceed kmax
            int colorIndex = 1;
            while (usedColors.contains(colorIndex) && colorIndex <= kmax) {
                colorIndex++;
            }

            // Set node color if a valid color is found
            if (colorIndex <= kmax) {
                node.setAttribute("color", colorIndex);
                node.setAttribute("ui.style", "fill-color: " + colors[colorIndex - 1] + ";");

                // Update the priority queue
                PriorityQueue<Node> updatedQueue = new PriorityQueue<>((n1, n2) -> {
                    int satDeg1 = getSaturationDegree(n1);
                    int satDeg2 = getSaturationDegree(n2);
                    if (satDeg1 != satDeg2) {
                        return Integer.compare(satDeg2, satDeg1); // Higher saturation degree first
                    }
                    int deg1 = n1.getDegree();
                    int deg2 = n2.getDegree();
                    return Integer.compare(deg2, deg1); // Higher degree first otherwise
                });

                updatedQueue.addAll(queue);

                queue = updatedQueue;
            }
        }

        // Assign colors to remaining uncolored nodes (if any) to minimize conflicts
        return colorizeUncoloredNodesWithMinConflicts(graph.nodes().toList(), kmax, colors);
    }

    /**
     * Applies the recursive Largest-First graph coloring algorithm to the specified graph.
     * Colors nodes using the generated color palette and minimizes conflicts.
     * Equivalent to calling recursiveLargestFirst(graph, (int) graph.getAttribute("kmax")).
     * @param graph the graph to be colored
     * @return the total number of conflicts after coloring
     */
    public static int recursiveLargestFirst(Graph graph) {
        return recursiveLargestFirst(graph, (int) graph.getAttribute("kmax"));
    }

    /**
     * Applies the recursive Largest-First graph coloring algorithm to the specified graph
     * with a custom maximum number of colors (kmax).
     *
     * @param graph the graph to be colored
     * @param kmax  the maximum number of colors to use
     * @return the total number of conflicts after coloring
     */
    public static int recursiveLargestFirst(Graph graph, int kmax) {
        String[] colors = generateColorPalette(kmax);
        for (Node node : graph) {
            node.setAttribute("color", -1);
        }
        recursiveLargestFirst(graph.nodes().collect(Collectors.toCollection(ArrayList::new)), colors, 1, kmax);
        return colorizeUncoloredNodesWithMinConflicts(graph.nodes().toList(), kmax, colors);
    }

    /**
     * Recursively applies the Largest-First algorithm to a list of nodes,
     * assigning colors to maximize the independence number of each step.
     *
     * @param nodes     the list of nodes to be colored
     * @param colors    the array of color codes in hexadecimal format
     * @param colorIndex the current index of the color to be assigned
     * @param kmax      the maximum number of colors to use
     */
    private static void recursiveLargestFirst(List<Node> nodes, String[] colors, int colorIndex, int kmax) {
        if (colorIndex > kmax) {
            return;
        }

        Set<Node> currentIndependentNodes = new HashSet<>();
        Node maxDegreeNode = findNodeWithMaxDegree(nodes);
        currentIndependentNodes.add(maxDegreeNode);
        nodes.remove(maxDegreeNode);

        // Find all nodes that are independent of maxDegreeNode
        Iterator<Node> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            Node n = iterator.next();
            boolean canAdd = true;
            for (Node nod : currentIndependentNodes) {
                if (nod.hasEdgeBetween(n)) {
                    canAdd = false;
                    break;
                }
            }
            if (canAdd) {
                currentIndependentNodes.add(n);
                iterator.remove();
            }
        }

        for (Node node : currentIndependentNodes) {
            node.setAttribute("color", colorIndex);
            node.setAttribute("ui.style", "fill-color: " + colors[colorIndex - 1] + ";");
        }
        recursiveLargestFirst(nodes, colors, colorIndex + 1, kmax);
    }

    /**
     * Finds the node with the maximum degree from a list of nodes.
     *
     * @param nodes the list of nodes from which to find the maximum degree node
     * @return the node with the maximum degree
     */
    private static Node findNodeWithMaxDegree(List<Node> nodes) {
        Node maxDegreeNode = null;
        int maxDegree = -1;
        for (Node node : nodes) {
            if (node.getDegree() > maxDegree) {
                maxDegree = node.getDegree();
                maxDegreeNode = node;
            }
        }
        return maxDegreeNode;
    }

    /**
     * Colors any remaining uncolored nodes with the color that minimizes conflicts.
     *
     * @param nodes  the list of nodes to be colored
     * @param kmax   the maximum number of colors to use
     * @param colors the array of color codes in hexadecimal format
     * @return the total number of conflicts after coloring
     */
    private static int colorizeUncoloredNodesWithMinConflicts(List<Node> nodes, int kmax, String[] colors) {
        int totalConflicts = 0;
        for (Node node : nodes) {
            if ((int) node.getAttribute("color") == -1) {
                int minConflicts = Integer.MAX_VALUE;
                int minConflictsColorIndex = 1;

                // Find the color with the minimum conflicts
                for (int colorIndex = 1; colorIndex <= kmax; colorIndex++) {
                    int conflicts = 0;
                    for (Edge edge : node.edges().toList()) {
                        if ((int) edge.getOpposite(node).getAttribute("color") == colorIndex) {
                            conflicts++;
                        }
                    }
                    if (conflicts < minConflicts) {
                        minConflicts = conflicts;
                        minConflictsColorIndex = colorIndex;
                    }
                }

                totalConflicts += minConflicts;

                // Assign the color with minimum conflicts
                node.setAttribute("color", minConflictsColorIndex);
                node.setAttribute("ui.style", "fill-color: " + colors[minConflictsColorIndex - 1] + ";");
            }
        }

        return totalConflicts;
    }
}
