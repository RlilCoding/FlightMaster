package fr.univlyon1.flightmaster.lib.Models;

/**
 * Represents a point in a 2D coordinate system with coordinates (x, y).
 */
public class Point {

    public double x; // X-coordinate of the point
    public double y; // Y-coordinate of the point

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
