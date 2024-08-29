package fr.univlyon1.flightmaster.lib.Exception;

/**
 * Exception thrown when there is an error exporting a graph.
 */
public class GraphExportException extends Exception {
    public GraphExportException(String message) {
        super(message);
    }

    public GraphExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
