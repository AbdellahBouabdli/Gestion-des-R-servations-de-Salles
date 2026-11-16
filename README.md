# Système Universitaire de Gestion des Réservations de Salles

Application Java Swing moderne développée dans le cadre de la **Partie 5 : Conception orientée objet en Java**.

---

## 🏛️ Architecture du Projet

L'application respecte les principes de conception orientée objet (SOLID, séparation des responsabilités) et est organisée en 4 packages :

```
src/main/java/
├── main/
│   ├── Main.java                 # Point d'entrée de l'application & initialisation des données de test
│   └── TestValidationMetier.java # Suite de tests automatisés (20/20) validant les règles métiers
├── model/
│   ├── StatutReservation.java    # Enum : CONFIRMEE, ANNULEE
│   ├── Salle.java                # Entité Salle avec validations strictes et équipements
│   ├── Enseignant.java           # Entité Enseignant avec validation regex de l'e-mail
│   └── Reservation.java          # Entité Réservation avec intégrité temporelle
├── service/
│   ├── GestionSalles.java        # Service CRUD Salles, recherches et intégrité référentielle
│   ├── GestionEnseignants.java   # Service Référentiel Enseignants
│   └── GestionReservations.java  # Moteur de détection des conflits horaires et annulation
└── ui/
    └── MainFrame.java            # Interface Graphique Swing moderne (FlatLaf / Nimbus)
```

---

## 🚀 Instructions d'exécution

### 1. Sous IntelliJ IDEA
1. Ouvrez IntelliJ IDEA -> **File** -> **Open...** -> Sélectionnez le dossier `tp`.
2. IntelliJ détecte automatiquement le projet Maven.
3. Exécutez la classe `main.Main` via le bouton vert **Run**.

### 2. Sous Eclipse
1. **File** -> **Import...** -> **Existing Maven Projects**.
2. Sélectionnez le dossier racine `tp`.
3. Clic droit sur `Main.java` -> **Run As** -> **Java Application**.

### 3. En Ligne de Commande (Terminal / PowerShell)
Compilation :
```bash
javac -encoding UTF-8 -d target/classes src/main/java/model/*.java src/main/java/service/*.java src/main/java/ui/*.java src/main/java/main/*.java
```

Lancement de l'application graphique :
```bash
java -cp target/classes main.Main
```

Exécution de la suite de tests automatisés (validation des 20 règles métiers) :
```bash
java -cp target/classes main.TestValidationMetier
```

---

## ✨ Fonctionnalités implémentées

1. **Gestion des Salles** :
   - Ajout, modification, suppression.
   - Validation stricte (capacité > 0, nom obligatoire).
   - Règle d'intégrité : suppression bloquée si des réservations actives (confirmées) sont associées.
   - Filtrage dynamique en temps réel par capacité minimale et par équipement.

2. **Gestion des Enseignants** :
   - Ajout, modification, suppression.
   - Validation stricte de l'e-mail par expression régulière.

3. **Gestion des Réservations & Conflits** :
   - Création de réservations sur créneau `yyyy-MM-dd HH:mm`.
   - Détection mathématique exacte des chevauchements d'intervalles (`debut < r.fin && fin > r.debut`).
   - Autorisation des réservations consécutives (ex: 10h-12h puis 12h-14h).
   - Annulation de réservation sans suppression physique (statut passe à `ANNULÉE`).
   - Libération automatique du créneau horaire après annulation.

4. **Interface Graphique Moderne (Java Swing)** :
   - Thème FlatLaf moderne (avec repli automatique sur Nimbus personnalisé).
   - Tableaux interactifs avec sélection et mise à jour dynamique des champs.
   - Badge de statut coloré (Vert pour CONFIRMÉE, Rouge pour ANNULÉE).
   - Boîtes de dialogue `JOptionPane` informatives pour toutes les alertes et erreurs métier.
