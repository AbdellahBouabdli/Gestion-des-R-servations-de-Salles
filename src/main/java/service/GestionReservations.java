package service;

import model.Enseignant;
import model.Reservation;
import model.Salle;
import model.StatutReservation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service centralisant la gestion des réservations, la détection des conflits
 * et l'intégrité temporelle des créneaux de salles.
 */
public class GestionReservations {
    private final List<Reservation> reservations = new ArrayList<>();

    /**
     * Crée et enregistre une nouvelle réservation après vérification complète des contraintes métier.
     *
     * @param id         Identifiant unique de la réservation
     * @param salle      Salle à réserver (doit exister et ne pas être nulle)
     * @param enseignant Enseignant demandeur (doit exister et ne pas être nul)
     * @param debut      Date et heure de début
     * @param fin        Date et heure de fin (postérieure au début)
     * @return La réservation confirmée créée
     * @throws IllegalArgumentException si les paramètres sont invalides
     * @throws IllegalStateException    si la salle n'est pas disponible (conflit de créneau)
     */
    public Reservation creerReservation(String id, Salle salle, Enseignant enseignant,
                                        LocalDateTime debut, LocalDateTime fin) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant de la réservation ne peut pas être vide.");
        }
        if (existeReservationId(id.trim())) {
            throw new IllegalArgumentException("Une réservation avec l'identifiant '" + id.trim() + "' existe déjà.");
        }
        if (salle == null) {
            throw new IllegalArgumentException("La salle est obligatoire pour créer une réservation.");
        }
        if (enseignant == null) {
            throw new IllegalArgumentException("L'enseignant est obligatoire pour créer une réservation.");
        }
        if (debut == null || fin == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont obligatoires.");
        }
        if (!fin.isAfter(debut)) {
            throw new IllegalArgumentException("La date de fin doit être strictement postérieure à la date de début.");
        }

        // Vérification des conflits horaires pour la salle demandée
        Optional<Reservation> conflit = detecterConflit(salle, debut, fin, null);
        if (conflit.isPresent()) {
            Reservation c = conflit.get();
            throw new IllegalStateException(String.format(
                    "Conflit de réservation : La salle '%s' est déjà réservée par %s du %s au %s.",
                    salle.getNom(),
                    c.getEnseignant().getNom(),
                    c.getDateDebut().format(Reservation.FORMATTER),
                    c.getDateFin().format(Reservation.FORMATTER)
            ));
        }

        Reservation nouvelleReservation = new Reservation(id.trim(), salle, enseignant, debut, fin, StatutReservation.CONFIRMEE);
        reservations.add(nouvelleReservation);
        return nouvelleReservation;
    }

    /**
     * Surcharge acceptant un objet Reservation déjà instancié.
     */
    public void creerReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("La réservation ne peut pas être null.");
        }
        if (existeReservationId(reservation.getId())) {
            throw new IllegalArgumentException("Une réservation avec l'ID '" + reservation.getId() + "' existe déjà.");
        }
        Optional<Reservation> conflit = detecterConflit(reservation.getSalle(), reservation.getDateDebut(), reservation.getDateFin(), null);
        if (conflit.isPresent()) {
            Reservation c = conflit.get();
            throw new IllegalStateException("Conflit de réservation avec la réservation ID: " + c.getId());
        }
        reservations.add(reservation);
    }

    /**
     * Détecte si un créneau horaire chevauche une réservation confirmée existante pour la même salle.
     * Permet les créneaux consécutifs (ex: 10h-12h puis 12h-13h).
     *
     * @param salle        Salle à vérifier
     * @param debut        Date et heure de début envisagée
     * @param fin          Date et heure de fin envisagée
     * @param idIgnore     Optionnel : identifiant d'une réservation à ignorer (ex: lors d'une modification)
     * @return Optional contenant la première réservation en conflit si trouvée, sinon Optional.empty()
     */
    public Optional<Reservation> detecterConflit(Salle salle, LocalDateTime debut, LocalDateTime fin, String idIgnore) {
        if (salle == null || debut == null || fin == null) {
            return Optional.empty();
        }

        return reservations.stream()
                .filter(r -> r.getStatut() == StatutReservation.CONFIRMEE)
                .filter(r -> r.getSalle().equals(salle))
                .filter(r -> idIgnore == null || !r.getId().equalsIgnoreCase(idIgnore))
                .filter(r -> r.chevauche(debut, fin))
                .findFirst();
    }

    /**
     * Vérifie la disponibilité d'une salle sur un créneau donné.
     *
     * @return true si la salle est entièrement libre sur l'intervalle [debut, fin], false sinon.
     */
    public boolean estSalleDisponible(Salle salle, LocalDateTime debut, LocalDateTime fin) {
        return detecterConflit(salle, debut, fin, null).isEmpty();
    }

    /**
     * Annule une réservation par son identifiant.
     * Le statut passe à ANNULEE sans supprimer l'objet de la collection (conservation d'historique).
     *
     * @param idReservation Identifiant de la réservation
     * @throws IllegalArgumentException si la réservation est introuvable
     * @throws IllegalStateException si la réservation est déjà annulée
     */
    public void annulerReservation(String idReservation) {
        Reservation reservation = getReservationParId(idReservation)
                .orElseThrow(() -> new IllegalArgumentException("Aucune réservation trouvée avec l'ID '" + idReservation + "'."));

        if (reservation.getStatut() == StatutReservation.ANNULEE) {
            throw new IllegalStateException("La réservation '" + idReservation + "' est déjà annulée.");
        }

        reservation.annuler();
    }

    /**
     * Recherche une réservation par son ID.
     */
    public Optional<Reservation> getReservationParId(String idReservation) {
        if (idReservation == null) return Optional.empty();
        return reservations.stream()
                .filter(r -> r.getId().equalsIgnoreCase(idReservation.trim()))
                .findFirst();
    }

    /**
     * Vérifie si un ID de réservation existe déjà.
     */
    public boolean existeReservationId(String id) {
        return getReservationParId(id).isPresent();
    }

    /**
     * Retourne la liste de toutes les réservations (confirmées et annulées).
     */
    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(new ArrayList<>(reservations));
    }

    /**
     * Affiche toutes les réservations sur la console.
     */
    public void afficherToutesLesReservations() {
        if (reservations.isEmpty()) {
            System.out.println("Aucune réservation enregistrée.");
            return;
        }
        System.out.println("=== Liste de toutes les réservations ===");
        reservations.forEach(Reservation::afficherInfos);
    }

    /**
     * Filtre les réservations associées à un enseignant spécifique.
     */
    public List<Reservation> getReservationsParEnseignant(String idEnseignant) {
        if (idEnseignant == null) return Collections.emptyList();
        return reservations.stream()
                .filter(r -> r.getEnseignant().getId().equalsIgnoreCase(idEnseignant.trim()))
                .collect(Collectors.toList());
    }

    /**
     * Filtre les réservations associées à une salle spécifique.
     */
    public List<Reservation> getReservationsParSalle(String idSalle) {
        if (idSalle == null) return Collections.emptyList();
        return reservations.stream()
                .filter(r -> r.getSalle().getId().equalsIgnoreCase(idSalle.trim()))
                .collect(Collectors.toList());
    }

    /**
     * Indique si une salle donnée possède au moins une réservation confirmée active.
     */
    public boolean aDesReservationsActivesPourSalle(String idSalle) {
        if (idSalle == null) return false;
        return reservations.stream()
                .anyMatch(r -> r.getSalle().getId().equalsIgnoreCase(idSalle.trim())
                        && r.getStatut() == StatutReservation.CONFIRMEE);
    }

    /**
     * Indique si un enseignant possède au moins une réservation confirmée active.
     */
    public boolean aDesReservationsActivesPourEnseignant(String idEnseignant) {
        if (idEnseignant == null) return false;
        return reservations.stream()
                .anyMatch(r -> r.getEnseignant().getId().equalsIgnoreCase(idEnseignant.trim())
                        && r.getStatut() == StatutReservation.CONFIRMEE);
    }
}
