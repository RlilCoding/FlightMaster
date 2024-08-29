package fr.univlyon1.flightmaster.app;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;
import java.io.IOException;

/**
 * Main class is the entry point of the Flight Master application.
 * It initializes the GUI and sets the look and feel.
 */
public class Main {
    /**
     * Main method is the entry point of the Flight Master application.
     * It initializes the GUI and sets the look and feel.
     * @param args Command-line arguments (not used).
     * @throws IOException If an error occurs while initializing the GUI.
     */
    public static void main(String[] args) throws IOException {
        // Set the system property for GraphStream UI to use Swing
        System.setProperty("org.graphstream.ui", "swing");

        // Set the look and feel to FlatMacDarkLaf theme
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        // Create an instance of the HomeFrame class to initialize the GUI
        new HomeFrame();
    }
}
