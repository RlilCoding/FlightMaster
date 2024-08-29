package fr.univlyon1.flightmaster.app;

import fr.univlyon1.flightmaster.lib.IO.IOService;
import fr.univlyon1.flightmaster.lib.Models.Airport;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Map;

/**
 * AirportChoicePanel class represents a JPanel that allows users to load an airport file.
 */
public class AirportChoicePanel extends JPanel {

    public static Map<String, Airport> airports = null;
    private JLabel airportFileNameLabel;

    /**
     * Constructor for the AirportChoicePanel class.
     * Initializes the panel layout and components.
     */
    public AirportChoicePanel() {
        this.setLayout(new GridLayout(3, 1));
        initializeComponents();
    }

    /**
     * Initializes the components of the panel.
     */
    private void initializeComponents() {
        // Create components
        JLabel title = createTitleLabel("Charger un fichier d'aéroports");
        airportFileNameLabel = new JLabel("Aucune liste d'aéroports");
        JButton loadAirportButton = createLoadAirportButton();

        // Add components to the panel
        this.add(title);
        this.add(loadAirportButton);
        this.add(airportFileNameLabel);
    }

    /**
     * Creates a title label with the specified text.
     *
     * @param text the text for the label.
     * @return the created JLabel.
     */
    private JLabel createTitleLabel(String text) {
        JLabel titleLabel = new JLabel(text);
        titleLabel.setFont(new Font("Arial", Font.CENTER_BASELINE, 18));
        return titleLabel;
    }

    /**
     * Creates a button for loading an airport file.
     *
     * @return the created JButton.
     */
    private JButton createLoadAirportButton() {
        JButton button = new JButton("Choisir un fichier");
        button.addActionListener(this::onLoadAirportButtonClick);
        return button;
    }

    /**
     * Handles the action when the load airport button is clicked.
     *
     * @param e the ActionEvent triggered by clicking the button.
     */
    private void onLoadAirportButtonClick(ActionEvent e) {
        File selectedFile = chooseFile();
        if (selectedFile != null) {
            loadAirportFile(selectedFile);
        }
    }

    /**
     * Opens a file chooser dialog to select a file.
     *
     * @return the selected file, or null if no file was selected.
     */
    private File chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Fichier TXT (*.txt)", "txt"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Fichier CSV (*.csv)", "csv"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setApproveButtonText("Ouvrir");
        chooser.setDialogTitle("Charger un fichier d'aéroport");
        chooser.showOpenDialog(this);

        return chooser.getSelectedFile();
    }

    /**
     * Loads the selected airport file and updates the UI accordingly.
     *
     * @param file the selected file.
     */
    private void loadAirportFile(File file) {
        airportFileNameLabel.setText(file.getName());
        try {
            airports = IOService.loadAirports(file.getAbsolutePath());
            if (airports == null || airports.isEmpty()) {
                showErrorMessage("Liste d'aéroport vide ou invalide");
                airportFileNameLabel.setText("Fichier sélectionné invalide");
            } else {
                // Activate another component or perform additional actions
                FileChoicePanel.dataChooserButton.setEnabled(true);
            }
        } catch (Exception exception) {
            showErrorMessage("Erreur lors du chargement du fichier : " + exception.getMessage());
            airportFileNameLabel.setText("Fichier sélectionné invalide");
        }
    }

    /**
     * Shows an error message dialog.
     *
     * @param message the error message to be displayed.
     */
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
