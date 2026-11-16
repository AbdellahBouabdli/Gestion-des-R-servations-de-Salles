package service;

import model.Salle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service gérant le parc des salles universitaires en mémoire.
 */
public class GestionSalles {
    private final Map<String, Salle> salles = new LinkedHashMap<>();
    private GestionReservations gestionReservations;

    public GestionSalles() {
    }

    public GestionSalles(GestionReservations gestionReservations) {
        this.gestionReservations = gestionReservations;
    }

    public void setGestionReservations(GestionReservations gestionReservations) {
        this.gestionReservations = gestionReservations;
    }

    /**
     * Ajoute une nouvelle salle en s'assurant de l'unicité de l'identifiant.
     *
     * @param salle Salle à ajouter
     * @throws IllegalArgumentException si la salle est nulle ou si l'ID existe déjà
     */
    public void ajouterSalle(Salle salle) {
        if (salle == null) {
            throw new IllegalArgumentException("La salle à ajouter ne peut pas être null.");
        }
        if (salles.containsKey(salle.getId())) {
            throw new IllegalArgumentException("Une salle avec l'identifiant '" + salle.getId() + "' existe déjà.");
        }
        salles.put(salle.getId(), salle);
    }

    /**
     * Modifie les informations d'une salle existante.
     *
     * @param salle Salle avec les nouvelles informations (même ID)
     * @throws IllegalArgumentException si la salle est introuvable
     */
    public void modifierSalle(Salle salle) {
        if (salle == null) {
            throw new IllegalArgumentException("La salle à modifier ne peut pas être null.");
        }
        if (!salles.containsKey(salle.getId())) {
            throw new IllegalArgumentException("Impossible de modifier : aucune salle trouvée avec l'identifiant '" + salle.getId() + "'.");
        }
        salles.put(salle.getId(), salle);
    }

    /**
     * Supprime une salle par son identifiant.
     * Règle de gestion : la suppression est strictement bloquée si la salle possède
     * des réservations actives (confirmées).
     *
     * @param idSalle Identifiant de la salle à supprimer
     * @throws IllegalArgumentException si la salle n'existe pas
     * @throws IllegalStateException    si des réservations actives y sont associées
     */
    public void supprimerSalle(String idSalle) {
        if (idSalle == null || !salles.containsKey(idSalle)) {
            throw new IllegalArgumentException("Impossible de supprimer : aucune salle trouvée avec l'identifiant '" + idSalle + "'.");
        }

        // Vérification de la contrainte d'intégrité métier
        if (gestionReservations != null && gestionReservations.aDesReservationsActivesPourSalle(idSalle)) {
            throw new IllegalStateException("Suppression refusée : la salle '" + idSalle + 
                    "' possède des réservations actives (confirmées). Annulez d'abord ses réservations.");
        }

        salles.remove(idSalle);
    }

    /**
     * Recherche une salle par son identifiant.
     */
    public Optional<Salle> getSalleParId(String idSalle) {
        if (idSalle == null) return Optional.empty();
        return Optional.ofNullable(salles.get(idSalle));
    }

    /**
     * Retourne la liste de toutes les salles enregistrées.
     */
    public List<Salle> getSalles() {
        return Collections.unmodifiableList(new ArrayList<>(salles.values()));
    }

    /**
     * Affiche l'ensemble des salles sur la sortie standard.
     */
    public void afficherToutesLesSalles() {
        if (salles.isEmpty()) {
            System.out.println("Aucune salle enregistrée.");
            return;
        }
        System.out.println("=== Liste de toutes les salles ===");
        salles.values().forEach(Salle::afficherInfos);
    }

    /**
     * Recherche les salles dont la capacité est supérieure ou égale à capaciteMin.
     *
     * @param capaciteMin Capacité minimale requise
     * @return Liste des salles répondant au critère
     */
    public List<Salle> rechercherParCapacite(int capaciteMin) {
        if (capaciteMin <= 0) {
            return getSalles();
        }
        return salles.values().stream()
                .filter(s -> s.getCapacite() >= capaciteMin)
                .collect(Collectors.toList());
    }

    /**
     * Recherche les salles disposant d'un équipement spécifique (recherche insensible à la casse).
     *
     * @param equipement Nom de l'équipement (ex: "vidéoprojecteur")
     * @return Liste des salles équipées
     */
    public List<Salle> rechercherParEquipement(String equipement) {
        if (equipement == null || equipement.trim().isEmpty()) {
            return getSalles();
        }
        return salles.values().stream()
                .filter(s -> s.contientEquipement(equipement))
                .collect(Collectors.toList());
    }

    /**
     * Recherche combinée par capacité minimale ET équipement (idéal pour les filtres UI dynamiques).
     */
    public List<Salle> filtrerSalles(int capaciteMin, String equipement) {
        return salles.values().stream()
                .filter(s -> capaciteMin <= 0 || s.getCapacite() >= capaciteMin)
                .filter(s -> equipement == null || equipement.trim().isEmpty() || s.contientEquipement(equipement))
                .collect(Collectors.toList());
    }
}
