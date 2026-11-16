package main;

import model.Enseignant;
import model.Salle;
import service.GestionEnseignants;
import service.GestionReservations;
import service.GestionSalles;
import ui.MainFrame;

import javax.swing.SwingUtilities;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Point d'entrée principal de l'application de Gestion des Réservations de Salles.
 * Initialise l'architecture des services, charge les données de démonstration
 * et démarre l'interface graphique Swing.
 */
public class Main {

    public static void main(String[] args) {
        // 1. Instanciation des services métiers
        GestionReservations gestionReservations = new GestionReservations();
        GestionSalles gestionSalles = new GestionSalles(gestionReservations);
        GestionEnseignants gestionEnseignants = new GestionEnseignants();

        // 2. Chargement du jeu de données de test (3 salles, 2 enseignants, 2 réservations)
        initialiserDonneesDeDemonstration(gestionSalles, gestionEnseignants, gestionReservations);

        // 3. Démarrage de l'IHM moderne dans l'Event Dispatch Thread (EDT) Swing
        SwingUtilities.invokeLater(() -> {
            MainFrame fenetre = new MainFrame(gestionSalles, gestionEnseignants, gestionReservations);
            fenetre.setVisible(true);
        });
    }

    /**
     * Initialise l'application avec un jeu de données conforme aux spécifications :
     * - 3 Salles universitaires aux caractéristiques et équipements variés
     * - 2 Enseignants avec adresses e-mail institutionnelles valides
     * - 2 Réservations confirmées non conflictuelles
     */
    private static void initialiserDonneesDeDemonstration(GestionSalles gestionSalles,
                                                         GestionEnseignants gestionEnseignants,
                                                         GestionReservations gestionReservations) {
        try {
            // A. Ajout de 3 Salles
            Salle amphiA = new Salle("S-AMPHI-A", "Amphithéâtre Alan Turing", 150,
                    List.of("Vidéoprojecteur 4K", "Micro HF", "Sonorisation", "Wi-Fi Haut Débit"));
            Salle salleTp = new Salle("S-LAB-B204", "Laboratoire TP Informatique B204", 35,
                    List.of("35 PC Linux", "Vidéoprojecteur", "Tableau blanc"));
            Salle salleTd = new Salle("S-TD-C105", "Salle de Travaux Dirigés C105", 25,
                    List.of("Écran interactif tactile", "Wi-Fi"));

            gestionSalles.ajouterSalle(amphiA);
            gestionSalles.ajouterSalle(salleTp);
            gestionSalles.ajouterSalle(salleTd);

            // B. Ajout de 2 Enseignants
            Enseignant profDupont = new Enseignant("ENS-01", "Dr. Thomas Dupont", "thomas.dupont@universite.fr");
            Enseignant profBenali = new Enseignant("ENS-02", "Pr. Sarah Benali", "sarah.benali@universite.fr");

            gestionEnseignants.ajouterEnseignant(profDupont);
            gestionEnseignants.ajouterEnseignant(profBenali);

            // C. Ajout de 2 Réservations
            LocalDateTime demain = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0);

            // Réservation 1 : Amphi Turing pour Dr. Dupont de 09:00 à 12:00
            gestionReservations.creerReservation(
                    "RES-2026-001",
                    amphiA,
                    profDupont,
                    demain.withHour(9),
                    demain.withHour(12)
            );

            // Réservation 2 : Salle TP B204 pour Pr. Benali de 14:00 à 17:00
            gestionReservations.creerReservation(
                    "RES-2026-002",
                    salleTp,
                    profBenali,
                    demain.withHour(14),
                    demain.withHour(17)
            );

            System.out.println("Données de démonstration initialisées avec succès :");
            System.out.println("- 3 salles enregistrées");
            System.out.println("- 2 enseignants enregistrés");
            System.out.println("- 2 réservations initiales confirmées");

        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation des données : " + e.getMessage());
        }
    }
}
