package model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Représente un enseignant ou un membre du corps professoral universitaire.
 */
public class Enseignant {
    private final String id;
    private String nom;
    private String email;

    // Expression régulière standard pour valider le format de l'e-mail
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Constructeur avec validation stricte du nom et de l'e-mail.
     *
     * @param id    Identifiant unique de l'enseignant (non null, non vide)
     * @param nom   Nom complet de l'enseignant (non null, non vide)
     * @param email Adresse électronique institutionnelle valide
     * @throws IllegalArgumentException si les champs sont invalides
     */
    public Enseignant(String id, String nom, String email) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant de l'enseignant ne peut pas être vide ou null.");
        }
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'enseignant ne peut pas être vide ou null.");
        }
        validerEmail(email);

        this.id = id.trim();
        this.nom = nom.trim();
        this.email = email.trim().toLowerCase();
    }

    private static void validerEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse e-mail de l'enseignant ne peut pas être vide.");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("L'adresse e-mail '" + email + "' n'est pas au format valide (ex: utilisateur@domaine.fr).");
        }
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'enseignant ne peut pas être vide ou null.");
        }
        this.nom = nom.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        validerEmail(email);
        this.email = email.trim().toLowerCase();
    }

    /**
     * Affiche les informations de l'enseignant sur la console.
     */
    public void afficherInfos() {
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        return String.format("Enseignant [ID: %s] %s (%s)", id, nom, email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enseignant that = (Enseignant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
