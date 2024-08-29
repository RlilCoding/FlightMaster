package fr.univlyon1.flightmaster.app;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

/**
 * HomeFrame class represents the main window of the Flight Master application.
 * It contains the sidebar on the left and various panels for file selection, settings, and statistics in the center.
 */
public class HomeFrame extends JFrame {
    /**
     * Constructor for the HomeFrame class.
     * Initializes the main window of the Flight Master application.
     * @throws IOException If an error occurs while reading the application icon image.
     */
    HomeFrame() throws IOException {
        // Window Initialization
        setTitle("Flight Master");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setLocationRelativeTo(null);

        // Adding an image for the application icon
        Image image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/img_app_icon.jpeg")));
        setIconImage(image);

        // Layout Configuration in Border Layout
        setLayout(new BorderLayout());

        // Adding the sidebar to the left of the window
        SideBar sideBar = new SideBar();
        getContentPane().add(sideBar, BorderLayout.WEST);

        // Creating the central panel which will be in BoxLayout
        JPanel center_pan = new JPanel();
        center_pan.setLayout(new BoxLayout(center_pan, BoxLayout.Y_AXIS));

        // Top Panel Creation in Box Layout
        JPanel top_pan = new JPanel();
        top_pan.setLayout(new BoxLayout(top_pan, BoxLayout.X_AXIS));
        top_pan.add(Box.createVerticalStrut(1));

        // Adding the file selection panel for graphs or flights
        FileChoicePanel panel_file = new FileChoicePanel();
        top_pan.add(panel_file);
        top_pan.add(Box.createVerticalStrut(1));

        // Adding the airport file selection panel
        AirportChoicePanel panel_airport = new AirportChoicePanel();
        top_pan.add(panel_airport);
        top_pan.add(Box.createVerticalStrut(1));

        // Bottom Panel Creation in Box Layout
        JPanel bottom_pan = new JPanel();
        bottom_pan.setLayout(new BoxLayout(bottom_pan, BoxLayout.X_AXIS));

        // Creating and adding the settings panel
        SettingsPanel settingsPanel_ = new SettingsPanel();
        bottom_pan.add(Box.createVerticalStrut(10));
        bottom_pan.add(settingsPanel_);
        bottom_pan.add(Box.createVerticalStrut(10));

        // Creating the statistics panel
        StatisticsPanel stat_panel = new StatisticsPanel();

        // Adding a separator, configuring it with color and a separator
        JPanel separator = new JPanel();
        Color color_sep = new Color(111, 142, 176);
        separator.setBackground(color_sep);
        bottom_pan.add(stat_panel);

        // Creating and configuring the title with an Arial font
        Label home_title = new Label("Flight Master");
        Font font = new Font("Arial", Font.BOLD, 24);
        home_title.setFont(font);

        // Adding the different components to the panel
        center_pan.add(home_title);
        center_pan.add(Box.createVerticalStrut(45));
        center_pan.add(top_pan);
        center_pan.add(separator);
        center_pan.add(bottom_pan);
        center_pan.add(Box.createVerticalStrut(25));

        // Adding the content panel to the window
        getContentPane().add(center_pan, BorderLayout.CENTER);

        // Making the window visible
        setVisible(true);
    }
}
