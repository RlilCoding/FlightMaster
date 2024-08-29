package fr.univlyon1.flightmaster.lib;

import fr.univlyon1.flightmaster.lib.Models.Airport;
import fr.univlyon1.flightmaster.lib.Models.Flight;
import fr.univlyon1.flightmaster.lib.Models.Point;
import fr.univlyon1.flightmaster.lib.Utils.CollisionUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CollisionUtilTests {

    private static Map<String, Airport> airports;

    @BeforeAll
    public static void setUp() {
        // airports needed for the tests
        airports = new HashMap<>();
        airports.put("MRS", new Airport("Marseille", new Point(420.387068, 4607.15017)));
        airports.put("BES", new Airport("Brest", new Point(-325.555103, 4213.34092)));
        airports.put("LYS", new Airport("Lyon", new Point(394.648936, 4429.95907)));
        airports.put("BOD", new Airport("Bordeaux", new Point(-56.4286114, 4518.10228)));
    }

    @Test
    public void testSegmentIntersection() {
        Point p1 = new Point(420.387068, 4607.15017);
        Point q1 = new Point(-325.555103, 4213.34092);
        Point p2 = new Point(394.648936, 4429.95907);
        Point q2 = new Point(-56.4286114, 4518.10228);

        Point intersection = CollisionUtil.findIntersection(p1, q1, p2, q2);

        assertNotNull(intersection, "The intersection point should not be null.");

        assertEquals(168.472108, intersection.x, 0.00001, "The x coordinate should be correct.");
        assertEquals(4474.15535, intersection.y, 0.00001, "The y coordinate should be correct.");

    }

    @Test
    public void testEstimatedTimeOfArrivalAtIntersection() {
        Point intersection = new Point(168.472108, 4474.15535);
        Flight flight = new Flight("AF000090", "MRS", "BES", LocalTime.of(7, 33), Duration.ofMinutes(81));
        Flight flight2 = new Flight("AF000132", "LYS", "BOD", LocalTime.of(7, 34), Duration.ofMinutes(47));

        LocalTime eta1 = CollisionUtil.estimatedTimeOfArrivalAtIntersection(intersection, flight, airports);
        LocalTime eta2 = CollisionUtil.estimatedTimeOfArrivalAtIntersection(intersection, flight2, airports);

        assertNotNull(eta1, "The estimated time of arrival should not be null.");
        assertNotNull(eta2, "The estimated time of arrival should not be null.");


        assertEquals(LocalTime.of(8, 0), eta1.truncatedTo(ChronoUnit.MINUTES), "The estimated time of arrival should be correct.");
        assertEquals(LocalTime.of(7, 57), eta2.truncatedTo(ChronoUnit.MINUTES), "The estimated time of arrival should be correct.");

    }
}
