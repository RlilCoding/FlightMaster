package fr.univlyon1.flightmaster.app;

import fr.univlyon1.flightmaster.lib.Models.Airport;
import fr.univlyon1.flightmaster.lib.Models.Flight;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

/**
 * Represents a frame that displays a map with airports and flight routes. (See AirportWaypoint)
 */
public class MapFrame extends JXMapViewer {

    public MapFrame() {
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        setTileFactory(tileFactory);
        PanMouseInputListener panListener = new PanMouseInputListener(this);
        this.addMouseListener(panListener);
        this.addMouseMotionListener(panListener);

        ZoomMouseWheelListenerCenter zoomListener = new ZoomMouseWheelListenerCenter(this);
        this.addMouseWheelListener(zoomListener);

        // Ajouter un écouteur pour centrer la carte en cliquant
        this.addMouseListener(new CenterMapListener(this));

        // Set the focus
        setZoom(13);
        setAddressLocation(new GeoPosition(46.5, 2));

        List<Painter<JXMapViewer>> painters = new ArrayList<>();

        Set<AirportWaypoint> waypoints = new HashSet<>();
        for (Airport airport : AirportChoicePanel.airports.values()){
            waypoints.add(new AirportWaypoint(airport));
        }
        WaypointPainter<AirportWaypoint> wpp = new WaypointPainter<>() {
            @Override
            protected void doPaint(Graphics2D g, JXMapViewer map, int width, int height) {
                for (AirportWaypoint waypoint : waypoints){
                    Point2D point = map.getTileFactory().geoToPixel(waypoint.getPosition(), map.getZoom());
                    Rectangle rectangle = map.getViewportBounds();
                    int buttonX = (int) (point.getX() - rectangle.getX());
                    int buttonY = (int) (point.getY() - rectangle.getY());

                    JButton button = waypoint.getButton();
                    button.setLocation(buttonX - button.getWidth() / 2, buttonY - button.getHeight());
                }


            }
        };
        wpp.setWaypoints(waypoints);
        painters.add(wpp);
        painters.add(SetFlightRoutes());

        for (AirportWaypoint wp : waypoints){
            add(wp.getButton());
        }

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        setOverlayPainter(painter);
    }

    public static Painter<JXMapViewer> SetFlightRoutes() {
        return new Painter<>() {
            @Override
            public void paint(Graphics2D g, final JXMapViewer map, final int w, final int h) {


                // do the drawing
                for (Flight flight : FileChoicePanel.flights) {
                    Airport departureAirport = AirportChoicePanel.airports.get(flight.getDepartureAirport());
                    Airport arrivalAirport = AirportChoicePanel.airports.get(flight.getArrivalAirport());
                    Point2D p1 = map.convertGeoPositionToPoint(new GeoPosition(departureAirport.getLatitude(), departureAirport.getLongitude()));
                    Point2D p2 = map.convertGeoPositionToPoint(new GeoPosition(arrivalAirport.getLatitude(), arrivalAirport.getLongitude()));

                    // do the drawing
                    g.setColor(Color.black);
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setStroke(new BasicStroke(0.5f));
                    g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
                }

            }

        };
    }
}