package fr.univlyon1.flightmaster.lib.Exception;

/**
 * Exception thrown when there is an error exporting statistics.
 */
public class StatsExportException extends Exception {
    public StatsExportException(String message) {
        super(message);
    }

    public StatsExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
