package fr.univlyon1.flightmaster.lib.Utils;

import fr.univlyon1.flightmaster.lib.Models.Airport;
import fr.univlyon1.flightmaster.lib.Models.Flight;
import fr.univlyon1.flightmaster.lib.Models.Point;

import java.time.LocalTime;
import java.util.Map;

import static java.lang.Double.max;
import static java.lang.Double.min;

/**
 * Utility class for geometric collision detection and flight intersection calculations.
 */
public class CollisionUtil {

    /**
     * Checks if point Q lies on segment PR.
     *
     * @param P Point P of the segment.
     * @param Q Point Q to check.
     * @param R Point R of the segment.
     * @return true if point Q lies on segment PR, false otherwise.
     */
    public static boolean isOnSegment(Point P, Point Q, Point R) {
        return (Q.x <= max(P.x, R.x) &&
                Q.x >= min(P.x, R.x) &&
                Q.y <= max(P.y, R.y) &&
                Q.y >= min(P.y, R.y));
    }

    /**
     * Calculates the orientation of the ordered triplet (P, Q, R).
     *
     * @param P Point P.
     * @param Q Point Q.
     * @param R Point R.
     * @return 0 if P, Q, R are collinear, 1 if clockwise, 2 if counterclockwise.
     */
    public static int orientation(Point P, Point Q, Point R) {
        int result = 0;
        double val = ((Q.y - P.y) * (R.x - Q.x)) - ((Q.x - P.x) * (R.y - Q.y));
        if (val > 0)
            result = 1;
        else if (val < 0)
            result = 2;
        return result;
    }

    /**
     * Checks if segment AB intersects with segment CD.
     *
     * @param A Point A of segment AB.
     * @param B Point B of segment AB.
     * @param C Point C of segment CD.
     * @param D Point D of segment CD.
     * @return true if segment AB intersects with segment CD, false otherwise.
     */
    public static boolean hasIntersection(Point A, Point B, Point C, Point D) {
        int oA = orientation(A, B, C);
        int oB = orientation(A, B, D);
        int oC = orientation(C, D, A);
        int oD = orientation(C, D, B);

        if (oA != oB && oC != oD)
            return true;

        if (oA == 0 && isOnSegment(A, C, B))
            return true;

        if (oB == 0 && isOnSegment(A, D, B))
            return true;

        if (oC == 0 && isOnSegment(C, A, D))
            return true;

        return oD == 0 && isOnSegment(C, B, D);
    }

    /**
     * Finds the intersection point of segments AB and CD if they intersect.
     * Note: This method internally checks for intersection, so there's no need to call hasIntersection(A, B, C, D) separately.
     *
     * @param A Point A of segment AB.
     * @param B Point B of segment AB.
     * @param C Point C of segment CD.
     * @param D Point D of segment CD.
     * @return Intersection Point if segments intersect, null otherwise.
     */
    public static Point findIntersection(Point A, Point B, Point C, Point D) {
        if (hasIntersection(A, B, C, D)) {
            double denom = (A.x - B.x) * (C.y - D.y) - (A.y - B.y) * (C.x - D.x);
            double x = ((A.x * B.y - A.y * B.x) * (C.x - D.x) - (A.x - B.x) * (C.x * D.y - C.y * D.x)) / denom;
            double y = ((A.x * B.y - A.y * B.x) * (C.y - D.y) - (A.y - B.y) * (C.x * D.y - C.y * D.x)) / denom;
            return new Point(x, y);
        }
        return null;
    }

    /**
     * Calculates the estimated time of arrival at a given intersection point for a flight.
     *
     * @param I        Intersection Point.
     * @param flight   Flight object representing the flight.
     * @param airports Map of airport codes to Airport
     * @return Estimated time of arrival at the intersection point.
     */
    public static LocalTime estimatedTimeOfArrivalAtIntersection(Point I, Flight flight, Map<String, Airport> airports) {
        // Retrieve departure airport's position
        Point dep = airports.get(flight.getDepartureAirport()).getPosition();
        // Retrieve arrival airport's position
        Point arr = airports.get(flight.getArrivalAirport()).getPosition();
        // Get departure time as LocalTime
        LocalTime departureTime = flight.getDepartureTime();

        // Calculate total flight duration in minutes
        long duration = flight.getDuration().toMinutes();

        // Calculate total flight distance
        double flightDistance = Math.sqrt((arr.x - dep.x) * (arr.x - dep.x) + (arr.y - dep.y) * (arr.y - dep.y));
        double avgSpeed = flightDistance / duration;

        // Calculate distance to intersection
        double intersectDistance = Math.sqrt((I.x - dep.x) * (I.x - dep.x) + (I.y - dep.y) * (I.y - dep.y));
        double timeToIntersect = intersectDistance / avgSpeed;

        // Calculate time to intersection as duration and add it to departure time
        return departureTime.plusMinutes((long) timeToIntersect);
    }
}
