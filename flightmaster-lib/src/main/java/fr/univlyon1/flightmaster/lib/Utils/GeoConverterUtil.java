package fr.univlyon1.flightmaster.lib.Utils;

import fr.univlyon1.flightmaster.lib.Models.Point;

/**
 * Utility class for converting geographic coordinates between different formats.
 */
public class GeoConverterUtil {
    private static final double EARTH_RADIUS = 6371;

    /**
     * Converts DMS (degrees, minutes, seconds) format to decimal degrees.
     * @param degreesStr degrees
     * @param minutesStr minutes
     * @param secondsStr seconds
     * @param directionStr direction ('N', 'S', 'E', 'O')
     * @return Decimal degrees
     */
    public static double convertDMSToDecimal(String degreesStr, String minutesStr, String secondsStr, String directionStr) {
        if (degreesStr == null || minutesStr == null || secondsStr == null || directionStr == null ||
                degreesStr.isEmpty() || minutesStr.isEmpty() || secondsStr.isEmpty() || directionStr.isEmpty()) {
            throw new IllegalArgumentException("None of the parameters can be null or empty.");
        }

        try {
            double degrees = Integer.parseInt(degreesStr);
            double minutes = Integer.parseInt(minutesStr);
            double seconds = Double.parseDouble(secondsStr);
            char direction = directionStr.charAt(0);

            if (direction != 'N' && direction != 'S' && direction != 'E' && direction != 'O') {
                throw new IllegalArgumentException("Direction must be one of 'N', 'S', 'E', 'O'.");
            }

            double decimalDegrees = degrees + minutes / 60.0 + seconds / 3600.0;
            if (direction == 'S' || direction == 'O') {
                decimalDegrees *= -1;
            }
            return decimalDegrees;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Degrees, minutes, and seconds must be valid numbers.", e);
        }
    }

    /**
     * Converts geographic coordinates to 2D Cartesian coordinates.
     * @param latitude Latitude in decimal degrees
     * @param longitude Longitude in decimal degrees
     * @return 2D coordinates in a Point object
     */
    public static Point convertGeoToCartesian(double latitude, double longitude) {
        double latitudeRadians = Math.toRadians(latitude);
        double longitudeRadians = Math.toRadians(longitude);

        double x = EARTH_RADIUS * Math.cos(latitudeRadians) * Math.sin(longitudeRadians);
        double y = EARTH_RADIUS * Math.cos(latitudeRadians) * Math.cos(longitudeRadians);

        return new Point(x, y);
    }
}
