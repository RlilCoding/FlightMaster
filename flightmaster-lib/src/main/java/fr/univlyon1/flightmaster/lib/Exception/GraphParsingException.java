package fr.univlyon1.flightmaster.lib.Exception;

/**
 * Exception thrown when there is an error parsing a graph.
 */
public class GraphParsingException extends Exception {
    public GraphParsingException(String message) {
        super(message);
    }

    public GraphParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
