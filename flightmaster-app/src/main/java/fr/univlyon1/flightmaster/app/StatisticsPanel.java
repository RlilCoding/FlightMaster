package fr.univlyon1.flightmaster.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * StatisticsPanel class represents a JPanel that displays the statistics of the graph and information about the current flight.
 */
public class StatisticsPanel extends JPanel {

    //Creating components (1 Jtable and 1 JtextPane) to use it later in the code
    public static JTable table = null;
    public static JTextPane stat_text = null;

    /**
     * Constructor for the StatisticsPanel class.
     * Initializes the panel layout and components.
     */
    public StatisticsPanel() {
        //Setting the layout to GridLayout for 3 rows and 1 column
        this.setLayout(new GridLayout(3, 1));
        initializeComponents();
    }

    /**
     * Initializes the components of the panel.
     */
    private void initializeComponents() {
        // Create components

        // Title label for the statistics panel
        Label stat_title = new Label("Statistiques du graphe :");

        // JTextPane to display information about the current flight
        stat_text = new JTextPane();
        stat_text.setText("Informations sur la possibilité de la coloration");
        stat_text.setEditable(false);

        // Create the data model for the table
        DefaultTableModel model = new DefaultTableModel();

        // Add columns to the table model
        model.addColumn("Noeuds");
        model.addColumn("Arêtes");
        model.addColumn("Degré moyen");
        model.addColumn("Diamètre");
        model.addColumn("Nombre de composants");

        // Add an empty row to the table model
        model.addRow(new Object[]{null, null, null, null, null});

        // Create the JTable with the table model
        table = new JTable(model);

        // Customize components
        stat_text.setBackground(new Color(40, 40, 40));
        Font font = new Font("Arial", Font.CENTER_BASELINE, 18);
        stat_title.setFont(font);

        // Add components to the panel
        this.add(stat_title);
        this.add(stat_text);
        this.add(new JScrollPane(table));
    }
}
