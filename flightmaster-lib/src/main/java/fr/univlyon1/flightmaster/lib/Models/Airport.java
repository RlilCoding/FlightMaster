package fr.univlyon1.flightmaster.lib.Models;


/**
 * Represents an airport with a name, and geographic position.
 */
public class Airport {

    private String nom;
    private Point position;
    private double latitude;
    private double longitude;

    public Airport(String nom, Point position) {
        this.nom = nom;
        this.position = position;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getNom() {
        return nom;
    }

    public Point getPosition() {
        return position;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Airport{" +
                "nom='" + nom + '\'' +
                ", position=" + position +
                '}';
    }
}
