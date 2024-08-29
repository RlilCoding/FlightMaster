package fr.univlyon1.flightmaster.lib.Models;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Represents a flight with its unique identifier, departure and arrival airports,
 * departure time, and duration.
 */
public class Flight {

    private String id; // Unique identifier of the flight
    private String departureAirport; // Departure airport code
    private String arrivalAirport; // Arrival airport code
    private LocalTime departureTime; // Departure time of the flight
    private Duration duration; // Duration of the flight


    public Flight(String id, String departureAirport, String arrivalAirport, LocalTime departureTime, Duration duration) {
        this.id = id;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureTime = departureTime;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }


    public String getArrivalAirport() {
        return arrivalAirport;
    }


    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
