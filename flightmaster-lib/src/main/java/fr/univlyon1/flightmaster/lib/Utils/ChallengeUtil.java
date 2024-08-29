package fr.univlyon1.flightmaster.lib.Utils;

import fr.univlyon1.flightmaster.lib.Algos.Coloring;
import fr.univlyon1.flightmaster.lib.Exception.GraphExportException;
import fr.univlyon1.flightmaster.lib.Exception.GraphParsingException;
import fr.univlyon1.flightmaster.lib.IO.IOService;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.Graphs;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Utility class for solving graph coloring challenges using various algorithms such as Welsh-Powell, DSATUR, and Recursive Largest First.
 */
public class ChallengeUtil {

    /**
     * Solves the graph coloring challenge by loading graphs from a specified folder,
     * applying multiple algorithms, and exporting results.
     *
     * @param graphFolderPath Path to the folder containing graph files.
     * @param outputFolderPath Path to the folder where output files will be saved.
     * @throws GraphParsingException If there is an error parsing a graph file.
     * @throws GraphExportException If there is an error exporting a graph file.
     */
    public static void solveChallenge(String graphFolderPath, String outputFolderPath) throws GraphParsingException, GraphExportException {
        List<Graph> graphs = new ArrayList<>();
        Path graphFolder = Path.of(graphFolderPath);
        List<String> filenames = new ArrayList<>();

        // Load files from graph-eval0.txt to graph-eval19.txt
        for (int i = 0; i < 20; i++) {
            String graphPath = graphFolder.resolve("graph-eval" + i + ".txt").toString();
            Graph graph = IOService.loadGraph(graphPath);
            filenames.add("graph-eval" + i + ".txt");
            graphs.add(graph);
        }

        List<Integer> results = new ArrayList<>();

        int i = 0;
        for (Graph graph : graphs) {
            Graph graphToSave = null;
            int minConflicts = Integer.MAX_VALUE;

            // Define the algorithms to be executed in order
            List<Function<Graph, Integer>> algorithms = Arrays.asList(
                    Coloring::welshPowell,
                    Coloring::DSATUR,
                    Coloring::recursiveLargestFirst
            );

            for (Function<Graph, Integer> algorithm : algorithms) {
                try {
                    Graph clonedGraph = Graphs.clone(graph);
                    int conflicts = executeWithTimeout(() -> algorithm.apply(clonedGraph), 25);
                    if (conflicts < minConflicts) {
                        minConflicts = conflicts;
                        graphToSave = clonedGraph;
                    }
                } catch (Exception e) {
                    System.err.println(algorithm.getClass().getSimpleName() + " algorithm timed out");
                }
            }
            results.add(minConflicts);
            IOService.exportGraphColoring(graphToSave, outputFolderPath + "/colo-eval" + i + ".txt");
            System.out.println("Done with graph-eval" + i + ".txt");
            i += 1;
        }
        IOService.exportConflicts(filenames, results, outputFolderPath + "/coloration-groupe1.3.csv");
    }

    /**
     * Executes a callable task with a specified timeout.
     *
     * @param callable      Callable task to execute.
     * @param timeoutSeconds Timeout period in seconds.
     * @return Result of the callable task.
     * @throws Exception If the task execution exceeds the specified timeout.
     */
    private static Integer executeWithTimeout(Callable<Integer> callable, int timeoutSeconds) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(callable);
        try {
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw e;
        } finally {
            executor.shutdownNow();
        }
    }
}
