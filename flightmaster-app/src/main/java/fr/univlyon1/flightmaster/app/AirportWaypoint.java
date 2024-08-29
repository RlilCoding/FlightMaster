package fr.univlyon1.flightmaster.app;

import fr.univlyon1.flightmaster.lib.Models.Airport;
import org.jxmapviewer.viewer.DefaultWaypoint;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Represents an airport waypoint on the map. (See MapFrame)
 */
public class AirportWaypoint extends DefaultWaypoint {
    private final Airport airport;

    private JButton button;


    public AirportWaypoint(Airport newAirport) {
        super(newAirport.getLatitude(), newAirport.getLongitude());
        this.airport = newAirport;

        createButton();
    }


    public Airport getAirport() {
        return airport;
    }


    public JButton getButton() {
        return button;
    }


    private void createButton() {
        button = new JButton();

        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.setContentAreaFilled(false);
        button.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/airport_icon.png"))));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setSize(new Dimension(24, 24));
        button.addActionListener(e -> JOptionPane.showMessageDialog(null, this.airport.getNom(), null, JOptionPane.INFORMATION_MESSAGE));
    }

}
