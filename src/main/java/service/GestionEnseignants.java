package service;

import model.Enseignant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service de gestion du référentiel des enseignants en mémoire.
 */
public class GestionEnseignants {
    private final Map<String, Enseignant> enseignants = new LinkedHashMap<>();

    /**
     * Enregistre un nouvel enseignant en s'assurant de l'unicité de son identifiant.
     *
     * @param enseignant Enseignant à enregistrer
     * @throws IllegalArgumentException si l'enseignant est null ou si l'ID existe déjà
     */
    public void ajouterEnseignant(Enseignant enseignant) {
        if (enseignant == null) {
            throw new IllegalArgumentException("L'enseignant à ajouter ne peut pas être null.");
        }
        if (enseignants.containsKey(enseignant.getId())) {
            throw new IllegalArgumentException("Un enseignant avec l'ID '" + enseignant.getId() + "' existe déjà.");
        }
        enseignants.put(enseignant.getId(), enseignant);
    }

    /**
     * Met à jour les informations d'un enseignant existant.
     */
    public void modifierEnseignant(Enseignant enseignant) {
        if (enseignant == null) {
            throw new IllegalArgumentException("L'enseignant ne peut pas être null.");
        }
        if (!enseignants.containsKey(enseignant.getId())) {
            throw new IllegalArgumentException("Impossible de modifier : aucun enseignant trouvé avec l'ID '" + enseignant.getId() + "'.");
        }
        enseignants.put(enseignant.getId(), enseignant);
    }

    /**
     * Supprime un enseignant par son identifiant.
     */
    public boolean supprimerEnseignant(String idEnseignant) {
        if (idEnseignant == null || !enseignants.containsKey(idEnseignant)) {
            return false;
        }
        enseignants.remove(idEnseignant);
        return true;
    }

    /**
     * Recherche un enseignant par son identifiant unique.
     */
    public Optional<Enseignant> getEnseignantParId(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(enseignants.get(id));
    }

    /**
     * Retourne la liste immuable de tous les enseignants enregistrés.
     */
    public List<Enseignant> getEnseignants() {
        return Collections.unmodifiableList(new ArrayList<>(enseignants.values()));
    }

    /**
     * Affiche tous les enseignants enregistrés.
     */
    public void afficherTousLesEnseignants() {
        if (enseignants.isEmpty()) {
            System.out.println("Aucun enseignant enregistré.");
            return;
        }
        System.out.println("=== Liste des enseignants ===");
        enseignants.values().forEach(Enseignant::afficherInfos);
    }
}
