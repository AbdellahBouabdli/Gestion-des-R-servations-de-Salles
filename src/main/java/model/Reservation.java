package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Représente la réservation d'une salle par un enseignant pour un créneau horaire précis.
 */
public class Reservation {
    private final String id;
    private final Salle salle;
    private final Enseignant enseignant;
    private final LocalDateTime dateDebut;
    private final LocalDateTime dateFin;
    private StatutReservation statut;

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Constructeur avec validation stricte de cohérence et de non-nullité.
     *
     * @param id          Identifiant unique de la réservation
     * @param salle       Salle réservée (non null)
     * @param enseignant  Enseignant responsable (non null)
     * @param dateDebut   Date et heure de début du créneau (non null)
     * @param dateFin     Date et heure de fin du créneau (doit être postérieure à dateDebut)
     * @param statut      Statut initial de la réservation (par défaut CONFIRMEE si null)
     * @throws IllegalArgumentException si les règles métiers ne sont pas respectées
     */
    public Reservation(String id, Salle salle, Enseignant enseignant,
                       LocalDateTime dateDebut, LocalDateTime dateFin, StatutReservation statut) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant de la réservation ne peut pas être vide ou null.");
        }
        if (salle == null) {
            throw new IllegalArgumentException("La salle associée à la réservation ne peut pas être nulle.");
        }
        if (enseignant == null) {
            throw new IllegalArgumentException("L'enseignant associé à la réservation ne peut pas être nul.");
        }
        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Les dates de début et de fin de réservation sont obligatoires.");
        }
        if (!dateFin.isAfter(dateDebut)) {
            throw new IllegalArgumentException("La date de fin (" + dateFin.format(FORMATTER) +
                    ") doit être strictement postérieure à la date de début (" + dateDebut.format(FORMATTER) + ").");
        }

        this.id = id.trim();
        this.salle = salle;
        this.enseignant = enseignant;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = (statut != null) ? statut : StatutReservation.CONFIRMEE;
    }

    /**
     * Constructeur pratique avec statut CONFIRMEE par défaut.
     */
    public Reservation(String id, Salle salle, Enseignant enseignant,
                       LocalDateTime dateDebut, LocalDateTime dateFin) {
        this(id, salle, enseignant, dateDebut, dateFin, StatutReservation.CONFIRMEE);
    }

    public String getId() {
        return id;
    }

    public Salle getSalle() {
        return salle;
    }

    public Enseignant getEnseignant() {
        return enseignant;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public StatutReservation getStatut() {
        return statut;
    }

    public void setStatut(StatutReservation statut) {
        if (statut == null) {
            throw new IllegalArgumentException("Le statut de la réservation ne peut pas être null.");
        }
        this.statut = statut;
    }

    /**
     * Passe le statut de la réservation à ANNULEE sans supprimer l'objet (conservation d'historique).
     */
    public void annuler() {
        this.statut = StatutReservation.ANNULEE;
    }

    /**
     * Indique si la réservation est active (confirmée).
     */
    public boolean estActive() {
        return this.statut == StatutReservation.CONFIRMEE;
    }

    /**
     * Détermine si cette réservation chevauche un créneau [debut, fin] donné.
     * Règle d'intersection d'intervalles semi-ouverts :
     * Deux créneaux se chevauchent ssi (debut < this.dateFin) ET (fin > this.dateDebut).
     * Les créneaux consécutifs (ex: 10h-12h et 12h-13h) ne se chevauchent PAS.
     */
    public boolean chevauche(LocalDateTime debut, LocalDateTime fin) {
        if (debut == null || fin == null) return false;
        return debut.isBefore(this.dateFin) && fin.isAfter(this.dateDebut);
    }

    /**
     * Affiche les détails complets de la réservation.
     */
    public void afficherInfos() {
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        return String.format("Réservation [%s] - %s réservée par %s du %s au %s | Statut : %s",
                id,
                salle.getNom(),
                enseignant.getNom(),
                dateDebut.format(FORMATTER),
                dateFin.format(FORMATTER),
                statut.getLibelle());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
