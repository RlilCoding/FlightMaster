package fr.univlyon1.flightmaster.app;

import fr.univlyon1.flightmaster.lib.Algos.Coloring;
import fr.univlyon1.flightmaster.lib.IO.IOService;
import fr.univlyon1.flightmaster.lib.Models.Flight;
import fr.univlyon1.flightmaster.lib.Utils.FlightGraphBuilderUtil;
import fr.univlyon1.flightmaster.lib.Utils.GraphStatsUtil;
import org.graphstream.graph.Graph;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

/**
 * FileChoicePanel class represents a JPanel that allows users to load a file
 * and perform operations on a graph or a list of flights.
 */
public class FileChoicePanel extends JPanel {

    public static Graph graph = null;
    public static JButton dataChooserButton = null;
    public static List<Flight> flights = null;

    /**
     * Constructor for the FileChoicePanel class.
     * Initializes the panel layout and components.
     */
    public FileChoicePanel() {
        this.setLayout(new GridLayout(4, 1));

        // Create title label
        JLabel titlePanel = new JLabel("Charger une liste de vols/graphe");

        // ComboBox for selecting file type
        JComboBox<String> fileTypeComboBox = new JComboBox<>();
        fileTypeComboBox.addItem("Liste de Vols");
        fileTypeComboBox.addItem("Graphe");

        // Button to start calculations
        JButton loadFileButton = new JButton("Démarrer les calculs");

        // Action listener for file type selection
        fileTypeComboBox.addActionListener(e -> {
            dataChooserButton.setEnabled(!fileTypeComboBox.getSelectedItem().equals("Liste de Vols") || AirportChoicePanel.airports != null);
        });

        // Action listener for starting calculations
        loadFileButton.addActionListener(e -> {
            int conflicts = 0;
            String selectedAlgo = SettingsPanel.algo_chooser.getSelectedItem().toString();
            int kmax = Integer.parseInt(SettingsPanel.kmax_sp.getValue().toString());

            // Execute the selected algorithm
            if (selectedAlgo.equals("Welsh-Powell")) {
                conflicts = Coloring.welshPowell(graph, kmax);
            } else if (selectedAlgo.equals("DSATUR")) {
                conflicts = Coloring.DSATUR(graph, kmax);
            } else if (selectedAlgo.equals("Recursive Largest First")) {
                conflicts = Coloring.recursiveLargestFirst(graph, kmax);
            }

            // Display the result of the calculation
            JOptionPane.showMessageDialog(null, "Calcul en cours", "Lancement des algorithmes de colorations", JOptionPane.INFORMATION_MESSAGE);
            graph.setAttribute("conflictsCount", conflicts);

            if (conflicts == 0) {
                StatisticsPanel.stat_text.setForeground(Color.GREEN);
                StatisticsPanel.stat_text.setText("Il n'y a pas de conflit lors de la coloration");
            } else {
                StatisticsPanel.stat_text.setForeground(new Color(215, 0, 0));
                StatisticsPanel.stat_text.setText("Il y a " + conflicts + " conflits lors de la coloration \n augmentez le K-max pour trouver une solution sans conflits");
            }

            System.out.println("calcul en cours");
        });

        // Label to display selected file name
        Label fileNameLabel = new Label("Aucun fichier selectionne");

        // Button to choose a file
        dataChooserButton = new JButton("Choisir un fichier");
        dataChooserButton.setEnabled(false);

        // Action listener for file chooser button
        dataChooserButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Fichier TXT (*.txt)", "txt"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Fichier CSV (*.csv)", "csv"));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setApproveButtonText("Ouvrir");
            fileChooser.setDialogTitle("Charger un fichier");

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile != null) {
                    fileNameLabel.setText(selectedFile.getName());
                    System.out.println(selectedFile.getName());
                    loadFileButton.setEnabled(true);

                    String selectedFileType = fileTypeComboBox.getSelectedItem().toString();
                    try {
                        if (selectedFileType.equals("Liste de Vols")) {
                            flights = IOService.loadFlights(selectedFile.getAbsolutePath());
                            graph = FlightGraphBuilderUtil.buildFlightGraph(AirportChoicePanel.airports, flights, Integer.parseInt(SettingsPanel.time_deadline.getValue().toString()));
                        } else if (selectedFileType.equals("Graphe")) {
                            graph = IOService.loadGraph(selectedFile.getAbsolutePath());
                            SettingsPanel.kmax_sp.setValue(graph.getAttribute("kmax"));
                        }
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(null, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        fileNameLabel.setText("Fichier selectionné invalide");
                    }

                    graph.setAttribute("fileName", selectedFile.getName());
                    updateStatisticsTable(graph);
                }
            }
        });

        // Customize the components
        loadFileButton.setBackground(new Color(20, 70, 135));
        titlePanel.setFont(new Font("Arial", Font.CENTER_BASELINE, 18));

        // Layout setup
        JPanel fileTypePanel = new JPanel(new GridLayout(1, 2));
        fileTypePanel.add(fileTypeComboBox);
        fileTypePanel.add(dataChooserButton);

        JPanel fileSelectionPanel = new JPanel(new GridLayout(2, 1));
        fileSelectionPanel.add(fileNameLabel);
        fileSelectionPanel.add(loadFileButton);

        this.add(titlePanel);
        this.add(fileTypePanel);
        this.add(fileSelectionPanel);
    }

    /**
     * Updates the statistics table with the graph data.
     *
     * @param graph the graph to be analyzed and displayed in the table.
     */
    private void updateStatisticsTable(Graph graph) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Noeuds");
        model.addColumn("Arêtes");
        model.addColumn("Degré");
        model.addColumn("Diamètre");
        model.addColumn("Nombre de composants");

        DecimalFormat df = new DecimalFormat("#.000000");
        String formattedAverageDegree = df.format(GraphStatsUtil.getAverageDegree(graph));
        String diameterStr = "Infinite";
        Double diameter = GraphStatsUtil.diameter(graph);

        if (diameter != null) {
            diameterStr = diameter.toString();
        }

        model.addRow(new Object[]{
                graph.getNodeCount(),
                graph.getEdgeCount(),
                formattedAverageDegree,
                diameterStr,
                GraphStatsUtil.getConnectComponentsCount(graph)
        });

        StatisticsPanel.table.setModel(model);
    }
}
