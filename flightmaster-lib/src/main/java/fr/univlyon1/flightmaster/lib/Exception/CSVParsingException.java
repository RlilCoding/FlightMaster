package fr.univlyon1.flightmaster.lib.Exception;

/**
 * Exception thrown when there is an error parsing CSV data.
 */
public class CSVParsingException extends Exception {


    public CSVParsingException(String message) {
        super(message);
    }

    public CSVParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
