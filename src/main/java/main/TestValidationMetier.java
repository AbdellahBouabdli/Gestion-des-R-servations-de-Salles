package main;

import model.Enseignant;
import model.Reservation;
import model.Salle;
import model.StatutReservation;
import service.GestionEnseignants;
import service.GestionReservations;
import service.GestionSalles;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Suite de tests automatisés validant l'ensemble des règles métiers et spécifications :
 * - Validations strictes des constructeurs (Salle, Enseignant, Reservation)
 * - Détection de conflit de créneaux (chevauchement vs réservations consécutives)
 * - Blocage de la suppression d'une salle avec réservations confirmées
 * - Déblocage après annulation
 * - Recherche par capacité et équipements
 */
public class TestValidationMetier {

    private static int testsPasses = 0;
    private static int testsTotal = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   DÉBUT DES TESTS DE VALIDATION DES RÈGLES MÉTIER");
        System.out.println("=================================================\n");

        testValidationsSalle();
        testValidationsEnseignant();
        testValidationsReservation();
        testConflitsHoraires();
        testRegleSuppressionSalle();
        testRechercheFiltres();

        System.out.println("\n=================================================");
        System.out.printf("   RÉSULTAT GLOBAL : %d / %d TESTS RÉUSSIS\n", testsPasses, testsTotal);
        System.out.println("=================================================");

        if (testsPasses != testsTotal) {
            System.exit(1);
        }
    }

    private static void verifier(String nomTest, boolean condition) {
        testsTotal++;
        if (condition) {
            testsPasses++;
            System.out.println("  [OK] " + nomTest);
        } else {
            System.err.println("  [ECHEC] " + nomTest);
        }
    }

    private static void testValidationsSalle() {
        System.out.println("--- Test 1 : Validations strictes de la classe Salle ---");

        // Capacité <= 0 doit lever IllegalArgumentException
        boolean capInvalide = false;
        try {
            new Salle("S1", "Salle Test", 0);
        } catch (IllegalArgumentException e) {
            capInvalide = true;
        }
        verifier("Capacité égale à 0 rejetée", capInvalide);

        // Nom vide doit lever IllegalArgumentException
        boolean nomVide = false;
        try {
            new Salle("S1", "   ", 20);
        } catch (IllegalArgumentException e) {
            nomVide = true;
        }
        verifier("Nom vide rejeté", nomVide);

        // Création valide
        Salle s = new Salle("S1", "Salle 101", 30, List.of("Projecteur", "Wi-Fi"));
        verifier("Création salle valide", s.getCapacite() == 30 && s.getEquipements().size() == 2);
    }

    private static void testValidationsEnseignant() {
        System.out.println("\n--- Test 2 : Validations de la classe Enseignant ---");

        // Format d'email invalide
        boolean emailInvalide = false;
        try {
            new Enseignant("E1", "Dupont", "email-invalide-sans-arobase");
        } catch (IllegalArgumentException e) {
            emailInvalide = true;
        }
        verifier("Email sans arobase rejeté", emailInvalide);

        // Nom vide
        boolean nomVide = false;
        try {
            new Enseignant("E1", "", "dupont@univ.fr");
        } catch (IllegalArgumentException e) {
            nomVide = true;
        }
        verifier("Nom vide enseignant rejeté", nomVide);

        // Enseignant valide
        Enseignant e = new Enseignant("E1", "Dr. Dupont", "dupont@univ.fr");
        verifier("Création enseignant valide", e.getEmail().equals("dupont@univ.fr"));
    }

    private static void testValidationsReservation() {
        System.out.println("\n--- Test 3 : Validations de la classe Reservation ---");

        Salle s = new Salle("S1", "Salle 1", 20);
        Enseignant e = new Enseignant("E1", "Dupont", "dupont@univ.fr");
        LocalDateTime maintenant = LocalDateTime.now();

        // Date fin antérieure ou égale à date début
        boolean dateInvalide = false;
        try {
            new Reservation("R1", s, e, maintenant.plusHours(2), maintenant.plusHours(1));
        } catch (IllegalArgumentException ex) {
            dateInvalide = true;
        }
        verifier("Date fin antérieure à date début rejetée", dateInvalide);

        boolean dateEgale = false;
        try {
            new Reservation("R2", s, e, maintenant, maintenant);
        } catch (IllegalArgumentException ex) {
            dateEgale = true;
        }
        verifier("Date fin égale à date début rejetée", dateEgale);
    }

    private static void testConflitsHoraires() {
        System.out.println("\n--- Test 4 : Règles de détection des conflits de créneaux ---");

        GestionReservations gr = new GestionReservations();
        Salle amphi = new Salle("AMPHI", "Amphithéâtre", 100);
        Enseignant ens1 = new Enseignant("E1", "Prof 1", "prof1@univ.fr");
        Enseignant ens2 = new Enseignant("E2", "Prof 2", "prof2@univ.fr");

        LocalDateTime base = LocalDateTime.of(2026, 11, 20, 10, 0); // 10h00
        LocalDateTime finBase = LocalDateTime.of(2026, 11, 20, 12, 0); // 12h00

        // Réservation 1 : 10h00 - 12h00
        gr.creerReservation("R1", amphi, ens1, base, finBase);
        verifier("Création première réservation 10h-12h", gr.getReservations().size() == 1);

        // Conflit 1 : Même créneau exact 10h00 - 12h00
        boolean conflitExact = false;
        try {
            gr.creerReservation("R2", amphi, ens2, base, finBase);
        } catch (IllegalStateException ex) {
            conflitExact = true;
        }
        verifier("Conflit créneau identique détecté", conflitExact);

        // Conflit 2 : Chevauchement partiel (11h00 - 13h00)
        boolean conflitPartiel = false;
        try {
            gr.creerReservation("R3", amphi, ens2, base.plusHours(1), base.plusHours(3));
        } catch (IllegalStateException ex) {
            conflitPartiel = true;
        }
        verifier("Conflit chevauchement partiel 11h-13h détecté", conflitPartiel);

        // RÈGLE SPÉCIALE : Réservations consécutives (12h00 - 14h00) doivent être AUTORISÉES !
        boolean consecutivesAutorisees = true;
        try {
            gr.creerReservation("R4", amphi, ens2, finBase, finBase.plusHours(2));
        } catch (Exception ex) {
            conflitExact = false;
            consecutivesAutorisees = false;
        }
        verifier("Réservations consécutives 10h-12h et 12h-14h AUTORISÉES", consecutivesAutorisees);

        // RÈGLE SPÉCIALE 2 : Réservations consécutives antérieures (08h00 - 10h00) doivent être AUTORISÉES !
        boolean consecutivesAvant = true;
        try {
            gr.creerReservation("R5", amphi, ens2, base.minusHours(2), base);
        } catch (Exception ex) {
            consecutivesAvant = false;
        }
        verifier("Réservations consécutives 08h-10h et 10h-12h AUTORISÉES", consecutivesAvant);

        // Annulation de R1 (10h-12h) -> la salle doit redevenir disponible sur 10h-12h !
        gr.annulerReservation("R1");
        verifier("Statut de R1 passé à ANNULEE", gr.getReservationParId("R1").get().getStatut() == StatutReservation.ANNULEE);

        boolean dispoApresAnnulation = gr.estSalleDisponible(amphi, base, finBase);
        verifier("Salle redevenue disponible sur 10h-12h après annulation", dispoApresAnnulation);
    }

    private static void testRegleSuppressionSalle() {
        System.out.println("\n--- Test 5 : Règle de suppression de salle liée aux réservations ---");

        GestionReservations gr = new GestionReservations();
        GestionSalles gs = new GestionSalles(gr);

        Salle s = new Salle("S-SUPPR", "Salle A Supprimer", 40);
        Enseignant e = new Enseignant("E-TEST", "Prof Test", "test@univ.fr");
        gs.ajouterSalle(s);

        LocalDateTime d1 = LocalDateTime.of(2026, 12, 1, 9, 0);
        LocalDateTime d2 = LocalDateTime.of(2026, 12, 1, 11, 0);
        gr.creerReservation("R-BLOQUANTE", s, e, d1, d2);

        // Suppression de salle avec réservation active -> DOIT ÊTRE BLOQUÉE
        boolean suppressionBloquee = false;
        try {
            gs.supprimerSalle("S-SUPPR");
        } catch (IllegalStateException ex) {
            suppressionBloquee = true;
        }
        verifier("Suppression bloquée car réservation active confirmée", suppressionBloquee);

        // Annulation de la réservation
        gr.annulerReservation("R-BLOQUANTE");

        // Suppression maintenant possible
        boolean suppressionReussie = false;
        try {
            gs.supprimerSalle("S-SUPPR");
            suppressionReussie = gs.getSalleParId("S-SUPPR").isEmpty();
        } catch (Exception ex) {
            suppressionReussie = false;
        }
        verifier("Suppression autorisée après annulation de la réservation", suppressionReussie);
    }

    private static void testRechercheFiltres() {
        System.out.println("\n--- Test 6 : Recherche et filtres de salles ---");

        GestionSalles gs = new GestionSalles();
        gs.ajouterSalle(new Salle("S1", "Petite", 15, List.of("Tableau")));
        gs.ajouterSalle(new Salle("S2", "Moyenne", 40, List.of("Vidéoprojecteur", "Tableau")));
        gs.ajouterSalle(new Salle("S3", "Grande", 100, List.of("Vidéoprojecteur", "Micro", "Sonorisation")));

        List<Salle> parCap = gs.rechercherParCapacite(30);
        verifier("Recherche capacité >= 30 renvoie 2 salles", parCap.size() == 2);

        List<Salle> parEq = gs.rechercherParEquipement("Vidéoprojecteur");
        verifier("Recherche équipement 'Vidéoprojecteur' renvoie 2 salles", parEq.size() == 2);

        List<Salle> filtreCombine = gs.filtrerSalles(50, "Micro");
        verifier("Filtre combiné (capacité >= 50 ET Micro) renvoie 1 salle", filtreCombine.size() == 1 && filtreCombine.get(0).getId().equals("S3"));
    }
}
