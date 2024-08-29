package fr.univlyon1.flightmaster.app;

import fr.univlyon1.flightmaster.lib.IO.IOService;
import fr.univlyon1.flightmaster.lib.Utils.ChallengeUtil;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.camera.Camera;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;

import static fr.univlyon1.flightmaster.app.FileChoicePanel.graph;
import static org.graphstream.ui.view.Viewer.CloseFramePolicy.CLOSE_VIEWER;

/**
 * SideBar class represents the sidebar panel in the application.
 * It contains buttons for various actions such as generating a graph, saving data, running challenges, and getting help. You can also display a map (See MapFrame for more infos)
 */
public class SideBar extends JPanel {



    static boolean boutonAppuye = false;
    static Viewer viewer = null;

    /**
     * Constructor for the SideBar class.
     * Initializes the sidebar layout and components.
     */
    public SideBar() {
        // Setting layout to BorderLayout
        this.setLayout(new BorderLayout());
        initializeComponents();
    }

    /**
     * Initializes the components of the sidebar.
     */
    private void initializeComponents() {
        // Create components

        // Déclaration d'un booléen pour suivre l'état du bouton
         // Déclaration de la variable Viewer en dehors de l'actionListener

        // Button to generate a graph
        JButton bt_graphe = new JButton("Generer un graphe");
        bt_graphe.setToolTipText("Générer un Graphe");
        bt_graphe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // Vérifier si le bouton a déjà été appuyé
                    if (boutonAppuye && viewer != null) {
                        viewer.close(); // Fermer la fenêtre précédente si elle existe
                    }

                    viewer = FileChoicePanel.graph.display();
                    viewer.setCloseFramePolicy(CLOSE_VIEWER);
                    View view = viewer.getDefaultView();

                    // Ajout du MouseWheelListener à la vue
                    Component viewComponent = (Component) view;
                    viewComponent.addMouseWheelListener(new MouseWheelListener() {
                        @Override
                        public void mouseWheelMoved(MouseWheelEvent e) {
                            e.consume();
                            int i = e.getWheelRotation();
                            double factor = Math.pow(1.25, i);
                            Camera cam = view.getCamera();
                            double zoom = cam.getViewPercent() * factor;

                            // Mettre à jour la caméra avec les nouvelles valeurs
                            cam.setViewPercent(zoom);
                        }
                    });

                    // Mettre à jour l'état du bouton
                    boutonAppuye = true;
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null, "Graphe non chargé", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        // Button to save data
        JButton bt_save = new JButton("Sauvegarde");
        bt_save.setToolTipText("Le Format sera : \n nomduFichier;noeuds;arêtes;degrés;nbComposants;diamètre;conflits");
        bt_save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (FileChoicePanel.graph == null || graph.getAttribute("conflictsCount") == null) {
                    JOptionPane.showMessageDialog(null, "Graphe non chargé ou calcul non effectué", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JFileChooser chooser = new JFileChooser();
                try {
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setApproveButtonText("Sauvegarder");
                    chooser.setDialogTitle("Enregistrer sous");
                    chooser.showOpenDialog(null);
                    IOService.exportStats(graph, chooser.getSelectedFile().getAbsolutePath());
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null, exception.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Button to run challenge mode
        JButton bt_mode = new JButton("Challenge");
        bt_mode.setToolTipText("Réaliser le Challenge");
        bt_mode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setMultiSelectionEnabled(false);
                chooser.setApproveButtonText("Ouvrir");
                chooser.setDialogTitle("Sélectionner un dossier de graphe");
                chooser.showOpenDialog(null);
                File inputFile = chooser.getSelectedFile();

                JFileChooser outputDirectoryChooser = new JFileChooser();
                outputDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                outputDirectoryChooser.setMultiSelectionEnabled(false);
                outputDirectoryChooser.setApproveButtonText("Ouvrir");
                outputDirectoryChooser.setDialogTitle("Sélectionner un dossier de sortie");
                outputDirectoryChooser.showOpenDialog(null);
                File outputDirectory = outputDirectoryChooser.getSelectedFile();

                try {
                    ChallengeUtil.solveChallenge(inputFile.getAbsolutePath(), outputDirectory.getAbsolutePath());
                    JOptionPane.showMessageDialog(null, "Challenge terminé", "Challenge état", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null, exception.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Button for help (It shows a message in which you see steps to follow to use our app)
        JButton bt_aide = new JButton("Aide");
        bt_aide.setToolTipText("Obtenir de l'aide sur le fonctionnement");
        bt_aide.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = "Pour pouvoir utiliser l'application, veuillez suivre ces étapes :\n\n"
                        + "1. Charger un fichier d'aéroports + fichier de vols. (Ou charger un fichier de graphes en sélectionnant l'option)\n"
                        + "2. Choisir votre K-max et une deadline ainsi que l'algorithme souhaité.(Par défaut : kmax = 5 / Deadline = 15)\n"
                        + "3. Cliquer sur 'Démarrer les calculs'.\n"
                        + "4. Ensuite, vous pourrez consulter les statistiques et visualiser votre graphe en utilisant le premier bouton situé dans la Side Bar.";
                JOptionPane.showMessageDialog(null, message, "Aide", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Button to show the map
        JButton bt_map = new JButton("Ouvrir la carte");
        bt_map.setToolTipText("Ouvrir la carte de france");
        bt_map.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (AirportChoicePanel.airports == null || graph == null){
                    JOptionPane.showMessageDialog(null, "Veuillez charger une liste d'aéroports et de vols avant d'afficher la carte.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JFrame frame_map = new JFrame();
                MapFrame mapFrameViewer = new MapFrame();
                frame_map.getContentPane().add(mapFrameViewer);

                // Set the frame settings
                frame_map.setSize(800, 600); // Set the size of the frame
                frame_map.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Ensure the application exits when the frame is closed
                frame_map.setVisible(true);

            }
        });

        // Sidebar title label
        Label side_label = new Label("Outil");
        side_label.setFont(new Font("Arial", Font.CENTER_BASELINE, 18));

        // Panel to hold the central buttons
        Panel bar_center = new Panel();
        bar_center.setLayout(new GridLayout(5, 1));

        // Customize components
        this.setBorder(new LineBorder(Color.WHITE));
        bt_save.setPreferredSize(new Dimension(100, 15));

        // Add components to the panel
        this.add(side_label, BorderLayout.NORTH);
        this.add(bar_center, BorderLayout.CENTER);
        bar_center.add(bt_graphe);
        bar_center.add(bt_save);
        bar_center.add(bt_mode);
        bar_center.add(bt_aide);
        bar_center.add(bt_map);
    }
}
