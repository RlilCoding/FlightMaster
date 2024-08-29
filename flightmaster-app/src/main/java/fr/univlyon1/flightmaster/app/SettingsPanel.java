package fr.univlyon1.flightmaster.app;

import javax.swing.*;
import java.awt.*;

/**
 * SettingsPanel class represents a JPanel that allows users to set the parameters for graph coloring algorithms.
 */
public class SettingsPanel extends JPanel {

    //Creating components (2 Jpinner and 1 ComboBox) to use it later in the code
    public static JSpinner time_deadline = null;
    public static JComboBox algo_chooser = null;
    public static JSpinner kmax_sp = null;

    /**
     * Constructor for the SettingsPanel class.
     * Initializes the panel layout and components.
     */
    public SettingsPanel() {
        //Setting the layout to a GridLayout of 4 rows and 1 column
        this.setLayout(new GridLayout(4, 1));
        initializeComponents();
    }

    /**
     * Initializes the components of the panel.
     */
    private void initializeComponents() {
        // Create components

        // Title label for the settings panel
        JLabel title_setting = new JLabel("Paramètres de coloration");

        // ComboBox for selecting the coloring algorithm
        algo_chooser = new JComboBox();
        algo_chooser.addItem("Welsh-Powell");
        algo_chooser.addItem("DSATUR");
        algo_chooser.addItem("Recursive Largest First");

        // JSpinner for selecting the maximum number of colors (kmax)
        JLabel kmax_label = new JLabel("K-max");
        kmax_sp = new JSpinner(new SpinnerNumberModel(5, 1, 10000, 1));

        // JSpinner for selecting the time deadline
        JLabel deadline_label = new JLabel("Deadline");
        time_deadline = new JSpinner(new SpinnerNumberModel(15, 0, 120, 5));

        // Customization of the title font
        Font font = new Font("Arial", Font.CENTER_BASELINE, 18);
        title_setting.setFont(font);

        // Adding components to the panel
        this.add(title_setting);
        this.add(algo_chooser);

        // Panel for kmax settings
        JPanel lay_first = new JPanel(new GridLayout(1, 2));
        lay_first.add(kmax_label);
        lay_first.add(kmax_sp);

        // Panel for deadline settings
        JPanel lay_second = new JPanel(new GridLayout(1, 2));
        lay_second.add(deadline_label);
        lay_second.add(time_deadline);

        //adding the components to "SettingsPanel"
        this.add(lay_first);
        this.add(lay_second);
    }
}
