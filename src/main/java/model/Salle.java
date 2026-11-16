package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Représente une salle de cours, de travaux pratiques ou un amphithéâtre au sein de l'université.
 */
public class Salle {
    private final String id;
    private String nom;
    private int capacite;
    private final List<String> equipements;

    /**
     * Constructeur avec validation stricte des paramètres.
     *
     * @param id          Identifiant unique de la salle (non null, non vide)
     * @param nom         Nom descriptif de la salle (non null, non vide)
     * @param capacite    Nombre maximal de places assises (strictement supérieur à 0)
     * @param equipements Liste des équipements matériels disponibles (peut être null ou vide)
     * @throws IllegalArgumentException si les règles de validation ne sont pas respectées
     */
    public Salle(String id, String nom, int capacite, List<String> equipements) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant de la salle ne peut pas être vide ou null.");
        }
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la salle ne peut pas être vide ou null.");
        }
        if (capacite <= 0) {
            throw new IllegalArgumentException("La capacité de la salle doit être strictement supérieure à 0.");
        }

        this.id = id.trim();
        this.nom = nom.trim();
        this.capacite = capacite;
        this.equipements = new ArrayList<>();
        if (equipements != null) {
            for (String eq : equipements) {
                if (eq != null && !eq.trim().isEmpty()) {
                    this.equipements.add(eq.trim());
                }
            }
        }
    }

    /**
     * Constructeur pratique sans équipements initiaux.
     */
    public Salle(String id, String nom, int capacite) {
        this(id, nom, capacite, Collections.emptyList());
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la salle ne peut pas être vide ou null.");
        }
        this.nom = nom.trim();
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        if (capacite <= 0) {
            throw new IllegalArgumentException("La capacité de la salle doit être strictement supérieure à 0.");
        }
        this.capacite = capacite;
    }

    /**
     * Retourne une copie de la liste des équipements pour préserver l'encapsulation.
     */
    public List<String> getEquipements() {
        return new ArrayList<>(equipements);
    }

    /**
     * Remplace l'ensemble des équipements de la salle.
     */
    public void setEquipements(List<String> nouveauxEquipements) {
        this.equipements.clear();
        if (nouveauxEquipements != null) {
            for (String eq : nouveauxEquipements) {
                if (eq != null && !eq.trim().isEmpty() && !this.equipements.contains(eq.trim())) {
                    this.equipements.add(eq.trim());
                }
            }
        }
    }

    /**
     * Ajoute un équipement s'il n'est pas déjà présent.
     */
    public void ajouterEquipement(String equipement) {
        if (equipement != null && !equipement.trim().isEmpty()) {
            String nettoye = equipement.trim();
            if (!this.equipements.contains(nettoye)) {
                this.equipements.add(nettoye);
            }
        }
    }

    /**
     * Supprime un équipement de la salle.
     */
    public boolean supprimerEquipement(String equipement) {
        if (equipement == null) return false;
        return this.equipements.remove(equipement.trim());
    }

    /**
     * Vérifie si la salle possède un équipement donné (insensible à la casse).
     */
    public boolean contientEquipement(String equipementRecherche) {
        if (equipementRecherche == null || equipementRecherche.trim().isEmpty()) {
            return false;
        }
        String recherche = equipementRecherche.trim().toLowerCase();
        for (String eq : equipements) {
            if (eq.toLowerCase().contains(recherche)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retourne la liste des équipements sous forme de chaîne séparée par des virgules.
     */
    public String getEquipementsFormatte() {
        if (equipements.isEmpty()) {
            return "Aucun équipement";
        }
        return String.join(", ", equipements);
    }

    /**
     * Affiche les informations complètes de la salle sur la sortie standard.
     */
    public void afficherInfos() {
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        return String.format("Salle [ID: %s] %s (Capacité: %d places) - Équipements: %s",
                id, nom, capacite, getEquipementsFormatte());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Salle salle = (Salle) o;
        return Objects.equals(id, salle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
