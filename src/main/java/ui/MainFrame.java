package ui;

import model.Enseignant;
import model.Reservation;
import model.Salle;
import model.StatutReservation;
import service.GestionEnseignants;
import service.GestionReservations;
import service.GestionSalles;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interface Graphique principale (Java Swing) moderne et ergonomique
 * pour l'application de Gestion des Réservations de Salles Universitaires.
 */
public class MainFrame extends JFrame {

    // Services métier
    private final GestionSalles gestionSalles;
    private final GestionEnseignants gestionEnseignants;
    private final GestionReservations gestionReservations;

    // Palette de couleurs moderne
    private static final Color PRIMARY_COLOR = new Color(37, 99, 235);     // Bleu moderne (#2563EB)
    private static final Color SUCCESS_COLOR = new Color(22, 163, 74);     // Vert (#16A34A)
    private static final Color DANGER_COLOR = new Color(220, 38, 38);      // Rouge (#DC2626)
    private static final Color WARNING_COLOR = new Color(217, 119, 6);     // Orange (#D97706)
    private static final Color BG_LIGHT = new Color(248, 250, 252);        // Fond gris très clair
    private static final Color TEXT_DARK = new Color(30, 41, 59);          // Texte sombre
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    // Composants Onglet 1 : Salles
    private JTextField txtSalleId;
    private JTextField txtSalleNom;
    private JSpinner spinSalleCapacite;
    private JTextField txtSalleEquipements;
    private DefaultTableModel modelTableSalles;
    private JTable tableSalles;
    private JSpinner spinFiltreCapacite;
    private JTextField txtFiltreEquipement;

    // Composants Onglet 2 : Enseignants
    private JTextField txtEnsId;
    private JTextField txtEnsNom;
    private JTextField txtEnsEmail;
    private DefaultTableModel modelTableEnseignants;
    private JTable tableEnseignants;

    // Composants Onglet 3 : Réservations
    private JTextField txtResId;
    private JComboBox<SalleComboItem> comboResSalle;
    private JComboBox<EnseignantComboItem> comboResEnseignant;
    private JTextField txtResDebut;
    private JTextField txtResFin;
    private DefaultTableModel modelTableReservations;
    private JTable tableReservations;
    private JComboBox<EnseignantComboItem> comboFiltreEnseignant;

    public MainFrame(GestionSalles gestionSalles, GestionEnseignants gestionEnseignants, GestionReservations gestionReservations) {
        this.gestionSalles = gestionSalles;
        this.gestionEnseignants = gestionEnseignants;
        this.gestionReservations = gestionReservations;

        initialiserLookEtFeel();
        initialiserFenetre();
        construireUI();
        rafraichirToutesLesVues();
    }

    /**
     * Tente d'appliquer FlatLaf pour un rendu moderne et épuré.
     * En cas d'absence de la dépendance, applique un fallback soigné sur Nimbus.
     */
    private void initialiserLookEtFeel() {
        try {
            Class<?> flatLafClass = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            LookAndFeel laf = (LookAndFeel) flatLafClass.getDeclaredConstructor().newInstance();
            UIManager.setLookAndFeel(laf);
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
        } catch (Throwable t) {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored2) {}
            }
        }
    }

    private void initialiserFenetre() {
        setTitle("Système Universitaire de Réservation de Salles");
        setSize(1100, 720);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void construireUI() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(BG_LIGHT);

        // Barre d'en-tête supérieure avec titre et icône stylisée
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel lblTitle = new JLabel("Gestionnaire de Salles & Réservations Universitaires");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Architecture Java Orientée Objet | Interface Swing Moderne");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(224, 231, 255));

        JPanel titleContainer = new JPanel(new GridLayout(2, 1, 2, 2));
        titleContainer.setOpaque(false);
        titleContainer.add(lblTitle);
        titleContainer.add(lblSubtitle);

        headerPanel.add(titleContainer, BorderLayout.WEST);

        // Onglets principaux avec onglets stylés
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_HEADER);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("  Gestion des Salles  ", creerOngletSalles());
        tabbedPane.addTab("  Gestion des Enseignants  ", creerOngletEnseignants());
        tabbedPane.addTab("  Gestion des Réservations  ", creerOngletReservations());

        contentPane.add(headerPanel, BorderLayout.NORTH);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(contentPane);
    }

    // =========================================================================
    // 1. ONGLET GESTION DES SALLES
    // =========================================================================
    private JPanel creerOngletSalles() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_LIGHT);

        // Formulaire à gauche
        JPanel formCard = creerCardPanel("Formulaire Salle");
        formCard.setPreferredSize(new Dimension(340, 0));
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtSalleId = new JTextField();
        txtSalleNom = new JTextField();
        spinSalleCapacite = new JSpinner(new SpinnerNumberModel(30, 1, 1000, 5));
        txtSalleEquipements = new JTextField();
        txtSalleEquipements.setToolTipText("Séparez les équipements par des virgules (ex: Vidéoprojecteur, Wi-Fi, Micro)");

        int row = 0;
        ajouterChamp(formCard, gbc, row++, "ID Salle :", txtSalleId);
        ajouterChamp(formCard, gbc, row++, "Nom de la salle :", txtSalleNom);
        ajouterChamp(formCard, gbc, row++, "Capacité (places) :", spinSalleCapacite);
        ajouterChamp(formCard, gbc, row++, "Équipements :", txtSalleEquipements);

        // Boutons d'action
        JPanel boutonsPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        boutonsPanel.setOpaque(false);

        JButton btnAjouter = creerBouton("Ajouter", PRIMARY_COLOR, Color.WHITE);
        JButton btnModifier = creerBouton("Modifier", WARNING_COLOR, Color.WHITE);
        JButton btnSupprimer = creerBouton("Supprimer", DANGER_COLOR, Color.WHITE);
        JButton btnVider = creerBouton("Vider", Color.GRAY, Color.WHITE);

        boutonsPanel.add(btnAjouter);
        boutonsPanel.add(btnModifier);
        boutonsPanel.add(btnSupprimer);
        boutonsPanel.add(btnVider);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 6, 6, 6);
        formCard.add(boutonsPanel, gbc);

        // Espace vide pour tasser en haut
        gbc.gridy = row;
        gbc.weighty = 1.0;
        formCard.add(new JLabel(), gbc);

        // Partie droite : Filtres et Tableau
        JPanel tableContainer = new JPanel(new BorderLayout(10, 10));
        tableContainer.setOpaque(false);

        // Barre de filtres en temps réel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel lblFiltreCap = new JLabel("Capacité min :");
        lblFiltreCap.setFont(FONT_BOLD);
        spinFiltreCapacite = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 5));
        spinFiltreCapacite.setPreferredSize(new Dimension(70, 26));

        JLabel lblFiltreEq = new JLabel("Équipement :");
        lblFiltreEq.setFont(FONT_BOLD);
        txtFiltreEquipement = new JTextField(12);

        JButton btnResetFiltres = new JButton("Réinitialiser filtres");
        btnResetFiltres.setFont(FONT_REGULAR);

        filterPanel.add(lblFiltreCap);
        filterPanel.add(spinFiltreCapacite);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(lblFiltreEq);
        filterPanel.add(txtFiltreEquipement);
        filterPanel.add(btnResetFiltres);

        // Tableau des salles
        String[] colonnes = {"ID", "Nom", "Capacité", "Équipements"};
        modelTableSalles = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableSalles = creerTableStylisee(modelTableSalles);
        JScrollPane scrollTable = new JScrollPane(tableSalles);
        scrollTable.setBorder(new LineBorder(new Color(226, 232, 240), 1));

        tableContainer.add(filterPanel, BorderLayout.NORTH);
        tableContainer.add(scrollTable, BorderLayout.CENTER);

        panel.add(formCard, BorderLayout.WEST);
        panel.add(tableContainer, BorderLayout.CENTER);

        // Événements Salles
        btnAjouter.addActionListener(e -> ajouterSalleDepuisFormulaire());
        btnModifier.addActionListener(e -> modifierSalleDepuisFormulaire());
        btnSupprimer.addActionListener(e -> supprimerSalleSelectionnee());
        btnVider.addActionListener(e -> viderFormulaireSalle());

        tableSalles.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableSalles.getSelectedRow() != -1) {
                remplirFormulaireSalleDepuisSelection();
            }
        });

        // Filtres temps réel
        spinFiltreCapacite.addChangeListener(e -> filtrerTableauSalles());
        txtFiltreEquipement.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrerTableauSalles(); }
            public void removeUpdate(DocumentEvent e) { filtrerTableauSalles(); }
            public void changedUpdate(DocumentEvent e) { filtrerTableauSalles(); }
        });
        btnResetFiltres.addActionListener(e -> {
            spinFiltreCapacite.setValue(0);
            txtFiltreEquipement.setText("");
            filtrerTableauSalles();
        });

        return panel;
    }

    private void ajouterSalleDepuisFormulaire() {
        try {
            String id = txtSalleId.getText().trim();
            String nom = txtSalleNom.getText().trim();
            int capacite = (Integer) spinSalleCapacite.getValue();
            List<String> equipements = parserEquipements(txtSalleEquipements.getText());

            Salle salle = new Salle(id, nom, capacite, equipements);
            gestionSalles.ajouterSalle(salle);

            JOptionPane.showMessageDialog(this, "Salle '" + nom + "' ajoutée avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            viderFormulaireSalle();
            rafraichirTableauSalles();
            rafraichirCombosReservation();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur de validation", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Une erreur inattendue est survenue : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierSalleDepuisFormulaire() {
        int selectedRow = tableSalles.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une salle dans le tableau à modifier.", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String id = txtSalleId.getText().trim();
            String nom = txtSalleNom.getText().trim();
            int capacite = (Integer) spinSalleCapacite.getValue();
            List<String> equipements = parserEquipements(txtSalleEquipements.getText());

            Salle salle = new Salle(id, nom, capacite, equipements);
            gestionSalles.modifierSalle(salle);

            JOptionPane.showMessageDialog(this, "Salle '" + id + "' modifiée avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            rafraichirTableauSalles();
            rafraichirCombosReservation();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur de validation", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerSalleSelectionnee() {
        int selectedRow = tableSalles.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner la salle à supprimer.", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idSalle = (String) modelTableSalles.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer définitivement la salle [" + idSalle + "] ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                gestionSalles.supprimerSalle(idSalle);
                JOptionPane.showMessageDialog(this, "Salle supprimée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                viderFormulaireSalle();
                rafraichirTableauSalles();
                rafraichirCombosReservation();
            } catch (IllegalStateException ex) {
                // Erreur métier : salle ayant des réservations confirmées actives
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Action impossible", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void remplirFormulaireSalleDepuisSelection() {
        int row = tableSalles.getSelectedRow();
        if (row != -1) {
            String id = (String) modelTableSalles.getValueAt(row, 0);
            gestionSalles.getSalleParId(id).ifPresent(salle -> {
                txtSalleId.setText(salle.getId());
                txtSalleNom.setText(salle.getNom());
                spinSalleCapacite.setValue(salle.getCapacite());
                txtSalleEquipements.setText(String.join(", ", salle.getEquipements()));
                txtSalleId.setEditable(false); // ID non modifiable en mode édition
            });
        }
    }

    private void viderFormulaireSalle() {
        txtSalleId.setText("");
        txtSalleId.setEditable(true);
        txtSalleNom.setText("");
        spinSalleCapacite.setValue(30);
        txtSalleEquipements.setText("");
        tableSalles.clearSelection();
    }

    private List<String> parserEquipements(String texte) {
        if (texte == null || texte.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(texte.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private void rafraichirTableauSalles() {
        filtrerTableauSalles();
    }

    private void filtrerTableauSalles() {
        int capMin = (Integer) spinFiltreCapacite.getValue();
        String eqRecherche = txtFiltreEquipement.getText().trim();

        List<Salle> liste = gestionSalles.filtrerSalles(capMin, eqRecherche);

        modelTableSalles.setRowCount(0);
        for (Salle s : liste) {
            modelTableSalles.addRow(new Object[]{
                    s.getId(),
                    s.getNom(),
                    s.getCapacite() + " places",
                    s.getEquipementsFormatte()
            });
        }
    }

    // =========================================================================
    // 2. ONGLET GESTION DES ENSEIGNANTS
    // =========================================================================
    private JPanel creerOngletEnseignants() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_LIGHT);

        // Formulaire Enseignant
        JPanel formCard = creerCardPanel("Formulaire Enseignant");
        formCard.setPreferredSize(new Dimension(340, 0));
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtEnsId = new JTextField();
        txtEnsNom = new JTextField();
        txtEnsEmail = new JTextField();

        int row = 0;
        ajouterChamp(formCard, gbc, row++, "ID Enseignant :", txtEnsId);
        ajouterChamp(formCard, gbc, row++, "Nom complet :", txtEnsNom);
        ajouterChamp(formCard, gbc, row++, "Adresse e-mail :", txtEnsEmail);

        // Boutons
        JPanel boutonsPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        boutonsPanel.setOpaque(false);

        JButton btnAjouter = creerBouton("Ajouter", PRIMARY_COLOR, Color.WHITE);
        JButton btnModifier = creerBouton("Modifier", WARNING_COLOR, Color.WHITE);
        JButton btnSupprimer = creerBouton("Supprimer", DANGER_COLOR, Color.WHITE);
        JButton btnVider = creerBouton("Vider", Color.GRAY, Color.WHITE);

        boutonsPanel.add(btnAjouter);
        boutonsPanel.add(btnModifier);
        boutonsPanel.add(btnSupprimer);
        boutonsPanel.add(btnVider);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        formCard.add(boutonsPanel, gbc);

        // Espace vide
        gbc.gridy = row;
        gbc.weighty = 1.0;
        formCard.add(new JLabel(), gbc);

        // Tableau Enseignants
        String[] colonnes = {"ID", "Nom & Prénom", "Adresse E-mail"};
        modelTableEnseignants = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableEnseignants = creerTableStylisee(modelTableEnseignants);
        JScrollPane scrollTable = new JScrollPane(tableEnseignants);
        scrollTable.setBorder(new LineBorder(new Color(226, 232, 240), 1));

        panel.add(formCard, BorderLayout.WEST);
        panel.add(scrollTable, BorderLayout.CENTER);

        // Événements
        btnAjouter.addActionListener(e -> ajouterEnseignantDepuisFormulaire());
        btnModifier.addActionListener(e -> modifierEnseignantDepuisFormulaire());
        btnSupprimer.addActionListener(e -> supprimerEnseignantSelectionne());
        btnVider.addActionListener(e -> viderFormulaireEnseignant());

        tableEnseignants.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableEnseignants.getSelectedRow() != -1) {
                int r = tableEnseignants.getSelectedRow();
                String id = (String) modelTableEnseignants.getValueAt(r, 0);
                gestionEnseignants.getEnseignantParId(id).ifPresent(ens -> {
                    txtEnsId.setText(ens.getId());
                    txtEnsId.setEditable(false);
                    txtEnsNom.setText(ens.getNom());
                    txtEnsEmail.setText(ens.getEmail());
                });
            }
        });

        return panel;
    }

    private void ajouterEnseignantDepuisFormulaire() {
        try {
            String id = txtEnsId.getText().trim();
            String nom = txtEnsNom.getText().trim();
            String email = txtEnsEmail.getText().trim();

            Enseignant enseignant = new Enseignant(id, nom, email);
            gestionEnseignants.ajouterEnseignant(enseignant);

            JOptionPane.showMessageDialog(this, "Enseignant '" + nom + "' enregistré avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            viderFormulaireEnseignant();
            rafraichirTableauEnseignants();
            rafraichirCombosReservation();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur de validation", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierEnseignantDepuisFormulaire() {
        int selectedRow = tableEnseignants.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un enseignant dans le tableau.", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String id = txtEnsId.getText().trim();
            String nom = txtEnsNom.getText().trim();
            String email = txtEnsEmail.getText().trim();

            Enseignant enseignant = new Enseignant(id, nom, email);
            gestionEnseignants.modifierEnseignant(enseignant);

            JOptionPane.showMessageDialog(this, "Enseignant mis à jour avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            rafraichirTableauEnseignants();
            rafraichirCombosReservation();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur de validation", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerEnseignantSelectionne() {
        int selectedRow = tableEnseignants.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un enseignant à supprimer.", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modelTableEnseignants.getValueAt(selectedRow, 0);

        if (gestionReservations.aDesReservationsActivesPourEnseignant(id)) {
            JOptionPane.showMessageDialog(this, "Impossible de supprimer cet enseignant : il a des réservations actives (confirmées).", "Action refusée", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirmez-vous la suppression de l'enseignant [" + id + "] ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            gestionEnseignants.supprimerEnseignant(id);
            JOptionPane.showMessageDialog(this, "Enseignant supprimé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            viderFormulaireEnseignant();
            rafraichirTableauEnseignants();
            rafraichirCombosReservation();
        }
    }

    private void viderFormulaireEnseignant() {
        txtEnsId.setText("");
        txtEnsId.setEditable(true);
        txtEnsNom.setText("");
        txtEnsEmail.setText("");
        tableEnseignants.clearSelection();
    }

    private void rafraichirTableauEnseignants() {
        modelTableEnseignants.setRowCount(0);
        for (Enseignant ens : gestionEnseignants.getEnseignants()) {
            modelTableEnseignants.addRow(new Object[]{
                    ens.getId(),
                    ens.getNom(),
                    ens.getEmail()
            });
        }
    }

    // =========================================================================
    // 3. ONGLET GESTION DES RÉSERVATIONS
    // =========================================================================
    private JPanel creerOngletReservations() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_LIGHT);

        // Formulaire Réservation à gauche
        JPanel formCard = creerCardPanel("Nouvelle Réservation");
        formCard.setPreferredSize(new Dimension(380, 0));
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtResId = new JTextField();
        comboResSalle = new JComboBox<>();
        comboResEnseignant = new JComboBox<>();

        LocalDateTime maintenant = LocalDateTime.now().withMinute(0).withSecond(0).plusHours(1);
        txtResDebut = new JTextField(maintenant.format(Reservation.FORMATTER));
        txtResFin = new JTextField(maintenant.plusHours(2).format(Reservation.FORMATTER));

        int row = 0;
        ajouterChamp(formCard, gbc, row++, "ID Réservation :", txtResId);
        ajouterChamp(formCard, gbc, row++, "Salle demandée :", comboResSalle);
        ajouterChamp(formCard, gbc, row++, "Enseignant responsable :", comboResEnseignant);
        ajouterChamp(formCard, gbc, row++, "Date début (yyyy-MM-dd HH:mm) :", txtResDebut);
        ajouterChamp(formCard, gbc, row++, "Date fin (yyyy-MM-dd HH:mm) :", txtResFin);

        // Boutons
        JPanel boutonsPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        boutonsPanel.setOpaque(false);

        JButton btnCreerRes = creerBouton("Confirmer la réservation", PRIMARY_COLOR, Color.WHITE);
        JButton btnViderRes = creerBouton("Vider", Color.GRAY, Color.WHITE);

        boutonsPanel.add(btnCreerRes);
        boutonsPanel.add(btnViderRes);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 6, 6, 6);
        formCard.add(boutonsPanel, gbc);

        // Espace vide
        gbc.gridy = row;
        gbc.weighty = 1.0;
        formCard.add(new JLabel(), gbc);

        // Tableau et Barre d'actions à droite
        JPanel tableContainer = new JPanel(new BorderLayout(10, 10));
        tableContainer.setOpaque(false);

        // Barre supérieure : Filtre par enseignant et Bouton Annuler
        JPanel topBar = new JPanel(new BorderLayout(10, 10));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JPanel filterBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterBox.setOpaque(false);
        JLabel lblFiltreEns = new JLabel("Filtrer par Enseignant :");
        lblFiltreEns.setFont(FONT_BOLD);
        comboFiltreEnseignant = new JComboBox<>();
        comboFiltreEnseignant.setPreferredSize(new Dimension(220, 28));

        filterBox.add(lblFiltreEns);
        filterBox.add(comboFiltreEnseignant);

        JButton btnAnnulerReservation = creerBouton("Annuler la réservation sélectionnée", DANGER_COLOR, Color.WHITE);
        btnAnnulerReservation.setPreferredSize(new Dimension(270, 32));

        topBar.add(filterBox, BorderLayout.WEST);
        topBar.add(btnAnnulerReservation, BorderLayout.EAST);

        // Tableau des réservations
        String[] colonnes = {"ID", "Salle", "Enseignant", "Date Début", "Date Fin", "Statut"};
        modelTableReservations = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableReservations = creerTableStylisee(modelTableReservations);

        // Renderer personnalisé pour le statut (badge vert / rouge)
        tableReservations.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(FONT_BOLD);
                String val = (value != null) ? value.toString() : "";
                if (val.equalsIgnoreCase("CONFIRMEE") || val.equalsIgnoreCase("Confirmée")) {
                    setForeground(SUCCESS_COLOR);
                    setText("✔ CONFIRMÉE");
                } else {
                    setForeground(DANGER_COLOR);
                    setText("✖ ANNULÉE");
                }
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                } else {
                    setBackground(Color.WHITE);
                }
                return c;
            }
        });

        JScrollPane scrollTable = new JScrollPane(tableReservations);
        scrollTable.setBorder(new LineBorder(new Color(226, 232, 240), 1));

        tableContainer.add(topBar, BorderLayout.NORTH);
        tableContainer.add(scrollTable, BorderLayout.CENTER);

        panel.add(formCard, BorderLayout.WEST);
        panel.add(tableContainer, BorderLayout.CENTER);

        // Événements Réservations
        btnCreerRes.addActionListener(e -> creerReservationDepuisFormulaire());
        btnViderRes.addActionListener(e -> viderFormulaireReservation());
        btnAnnulerReservation.addActionListener(e -> annulerReservationSelectionnee());
        comboFiltreEnseignant.addActionListener(e -> rafraichirTableauReservations());

        return panel;
    }

    private void creerReservationDepuisFormulaire() {
        try {
            String id = txtResId.getText().trim();
            if (id.isEmpty()) {
                throw new IllegalArgumentException("L'identifiant de la réservation est obligatoire.");
            }

            SalleComboItem salleItem = (SalleComboItem) comboResSalle.getSelectedItem();
            if (salleItem == null) {
                throw new IllegalArgumentException("Veuillez sélectionner une salle valide.");
            }

            EnseignantComboItem enseignantItem = (EnseignantComboItem) comboResEnseignant.getSelectedItem();
            if (enseignantItem == null) {
                throw new IllegalArgumentException("Veuillez sélectionner un enseignant valide.");
            }

            LocalDateTime debut;
            LocalDateTime fin;
            try {
                debut = LocalDateTime.parse(txtResDebut.getText().trim(), Reservation.FORMATTER);
                fin = LocalDateTime.parse(txtResFin.getText().trim(), Reservation.FORMATTER);
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Format de date invalide. Veuillez utiliser le format : yyyy-MM-dd HH:mm (ex: 2026-11-20 14:00)");
            }

            // Appel au service métier qui vérifie les conflits et la cohérence
            gestionReservations.creerReservation(id, salleItem.salle, enseignantItem.enseignant, debut, fin);

            JOptionPane.showMessageDialog(this,
                    "Réservation créée avec succès pour la salle '" + salleItem.salle.getNom() + "' !",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);

            viderFormulaireReservation();
            rafraichirTableauReservations();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur de validation", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException ex) {
            // Conflit d'horaire ou chevauchement
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Conflit de réservation détecté", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur imprévue : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void annulerReservationSelectionnee() {
        int selectedRow = tableReservations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner la réservation à annuler dans le tableau.", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idReservation = (String) modelTableReservations.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous certain de vouloir annuler la réservation [" + idReservation + "] ?\n(L'historique sera conservé en statut 'ANNULÉE')",
                "Confirmation d'annulation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                gestionReservations.annulerReservation(idReservation);
                JOptionPane.showMessageDialog(this, "La réservation [" + idReservation + "] a été annulée.", "Réservation Annulée", JOptionPane.INFORMATION_MESSAGE);
                rafraichirTableauReservations();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viderFormulaireReservation() {
        txtResId.setText("");
        LocalDateTime maintenant = LocalDateTime.now().withMinute(0).withSecond(0).plusHours(1);
        txtResDebut.setText(maintenant.format(Reservation.FORMATTER));
        txtResFin.setText(maintenant.plusHours(2).format(Reservation.FORMATTER));
        tableReservations.clearSelection();
    }

    private void rafraichirTableauReservations() {
        modelTableReservations.setRowCount(0);

        EnseignantComboItem ensFiltre = (EnseignantComboItem) comboFiltreEnseignant.getSelectedItem();
        List<Reservation> list;

        if (ensFiltre == null || ensFiltre.enseignant == null) {
            list = gestionReservations.getReservations();
        } else {
            list = gestionReservations.getReservationsParEnseignant(ensFiltre.enseignant.getId());
        }

        for (Reservation r : list) {
            modelTableReservations.addRow(new Object[]{
                    r.getId(),
                    r.getSalle().getNom() + " (" + r.getSalle().getId() + ")",
                    r.getEnseignant().getNom(),
                    r.getDateDebut().format(Reservation.FORMATTER),
                    r.getDateFin().format(Reservation.FORMATTER),
                    r.getStatut().name()
            });
        }
    }

    private void rafraichirCombosReservation() {
        // Combo Salles
        comboResSalle.removeAllItems();
        for (Salle s : gestionSalles.getSalles()) {
            comboResSalle.addItem(new SalleComboItem(s));
        }

        // Combo Enseignants
        comboResEnseignant.removeAllItems();
        for (Enseignant e : gestionEnseignants.getEnseignants()) {
            comboResEnseignant.addItem(new EnseignantComboItem(e));
        }

        // Combo Filtre Enseignants (avec option "Tous")
        EnseignantComboItem selectionActuelle = (EnseignantComboItem) comboFiltreEnseignant.getSelectedItem();
        comboFiltreEnseignant.removeAllItems();
        comboFiltreEnseignant.addItem(new EnseignantComboItem(null)); // "Tous les enseignants"
        for (Enseignant e : gestionEnseignants.getEnseignants()) {
            comboFiltreEnseignant.addItem(new EnseignantComboItem(e));
        }

        if (selectionActuelle != null && selectionActuelle.enseignant != null) {
            for (int i = 0; i < comboFiltreEnseignant.getItemCount(); i++) {
                EnseignantComboItem item = comboFiltreEnseignant.getItemAt(i);
                if (item.enseignant != null && item.enseignant.getId().equals(selectionActuelle.enseignant.getId())) {
                    comboFiltreEnseignant.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void rafraichirToutesLesVues() {
        rafraichirTableauSalles();
        rafraichirTableauEnseignants();
        rafraichirCombosReservation();
        rafraichirTableauReservations();
    }

    // =========================================================================
    // UTILITAIRES GRAPHIQUES
    // =========================================================================
    private JPanel creerCardPanel(String titre) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1),
                new CompoundBorder(
                        BorderFactory.createTitledBorder(
                                BorderFactory.createEmptyBorder(),
                                titre,
                                TitledBorder.LEFT,
                                TitledBorder.TOP,
                                FONT_HEADER,
                                PRIMARY_COLOR
                        ),
                        new EmptyBorder(10, 10, 10, 10)
                )
        ));
        return card;
    }

    private void ajouterChamp(JPanel container, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(TEXT_DARK);
        container.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        comp.setFont(FONT_REGULAR);
        if (comp instanceof JTextField) {
            comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, 30));
        }
        container.add(comp, gbc);
    }

    private JButton creerBouton(String texte, Color fond, Color texteCouleur) {
        JButton btn = new JButton(texte);
        btn.setFont(FONT_BOLD);
        btn.setBackground(fond);
        btn.setForeground(texteCouleur);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 34));
        return btn;
    }

    private JTable creerTableStylisee(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(FONT_REGULAR);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(241, 245, 249));
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(TEXT_DARK);
        header.setPreferredSize(new Dimension(0, 34));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        return table;
    }

    // Classes internes pour l'affichage propre dans les JComboBox
    private static class SalleComboItem {
        final Salle salle;

        SalleComboItem(Salle salle) {
            this.salle = salle;
        }

        @Override
        public String toString() {
            return salle.getNom() + " (" + salle.getId() + " - " + salle.getCapacite() + " pl.)";
        }
    }

    private static class EnseignantComboItem {
        final Enseignant enseignant;

        EnseignantComboItem(Enseignant enseignant) {
            this.enseignant = enseignant;
        }

        @Override
        public String toString() {
            if (enseignant == null) {
                return "-- Tous les enseignants --";
            }
            return enseignant.getNom() + " (" + enseignant.getId() + ")";
        }
    }
}
