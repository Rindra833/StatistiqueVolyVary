# Voly-Vary — Travaux effectués

Dernière mise à jour : 10 juillet 2026

## Vue d'ensemble

| Phase | Domaine | État |
|---|---|---|
| Phase A | Configuration PostgreSQL et sécurité Spring | ✅ Terminée |
| Phase B | API REST et opérations CRUD | ✅ Terminée |
| Phase C | Connexion du frontend au backend | ✅ Terminée |
| Refactorisation | Déplacement du HTML généré vers les fichiers `index.html` | ✅ Terminée |
| Validation | Tests Java, PostgreSQL, sécurité et JavaScript | ✅ Terminée |

## Phase A — Sécurité et base de données

- [x] Vérification de la configuration PostgreSQL dans `application.properties`.
- [x] Configuration de la base `volyvary_db` sur le port `5432`.
- [x] Activation de `spring.jpa.hibernate.ddl-auto=update`.
- [x] Configuration du dialecte PostgreSQL demandé.
- [x] Désactivation de `spring.jpa.open-in-view` pour mieux séparer la couche web de la couche de persistance.
- [x] Création de `CustomUserDetailsService` pour charger les utilisateurs depuis `UtilisateurRepository`.
- [x] Ajout du bean `BCryptPasswordEncoder`.
- [x] Implémentation correcte des méthodes `UserDetails` dans `Utilisateur` :
  - `getUsername()` ;
  - `getPassword()` ;
  - `getAuthorities()`.
- [x] Correction du setter `setMdp()` dans `LoginRequest`.
- [x] Nettoyage et sécurisation de `AuthController`.
- [x] Retour d'une réponse HTTP `401 Unauthorized` lorsque les identifiants sont incorrects.
- [x] Persistance de l'authentification dans la session Spring Security.
- [x] Autorisation publique des ressources nécessaires à la page de connexion.
- [x] Protection des pages métier et des endpoints REST.
- [x] Invalidation de la session serveur lors de la déconnexion.
- [x] Suppression de la dépendance Spring Security déclarée deux fois dans `pom.xml`.
- [x] Ajout d'un mécanisme optionnel d'amorçage du premier administrateur avec des variables d'environnement.

### Amorçage du premier administrateur

Aucun mot de passe par défaut n'est stocké dans le dépôt. Avant le premier démarrage, les variables suivantes peuvent être définies :

```powershell
$env:VOLYVARY_ADMIN_NOM="admin"
$env:VOLYVARY_ADMIN_PASSWORD="un-mot-de-passe-solide"
$env:VOLYVARY_ADMIN_ROLE="Administrateur"
mvn spring-boot:run
```

Le compte est créé uniquement si le nom indiqué n'existe pas déjà.

## Phase B — API REST CRUD

### Entités normalisées

- [x] Ajout des getters et setters nécessaires à la sérialisation JSON.
- [x] Normalisation des noms de propriétés Java : `nom`, `email` et `adresse`.
- [x] Ajout de la propriété `quantite` dans `Fourniture`.
- [x] Ajout des propriétés d'identification et de relation manquantes dans `Utilisateur`.
- [x] Protection du mot de passe contre son exposition dans les réponses JSON.
- [x] Conservation de la séparation entre `Employee` et `Utilisateur`.

### Repositories créés ou complétés

- [x] `UtilisateurRepository`
- [x] `LivreurRepository`
- [x] `FournitureRepository`
- [x] `EmployeeRepository`
- [x] `ClientRepository`

### Contrôleurs REST créés

| Ressource | Endpoint principal | Opérations disponibles |
|---|---|---|
| Livreurs | `/api/livreurs` | GET, GET par ID, POST, PUT, DELETE |
| Fournitures | `/api/fournitures` | GET, GET par ID, POST, PUT, DELETE |
| Employees | `/api/employees` | GET, GET par ID, POST, PUT, DELETE |
| Clients | `/api/clients` | GET, GET par ID, POST, PUT, DELETE |
| Utilisateurs | `/api/utilisateurs` | GET, GET par ID, POST, PUT, DELETE |
| Authentification | `/api/auth/login` | POST |

### Règles particulières pour les utilisateurs

- [x] Création de `UtilisateurRequest` pour les données entrantes.
- [x] Création de `UtilisateurResponse` pour les données retournées.
- [x] Encodage BCrypt obligatoire lors de la création d'un utilisateur.
- [x] Réencodage du mot de passe uniquement lorsqu'un nouveau mot de passe est fourni pendant une modification.
- [x] Association optionnelle d'un compte à un `Employee` avec `employeeId`.
- [x] Aucun mot de passe ni hash BCrypt n'est retourné par l'API.

## Phase C — Intégration frontend/backend

### Module d'accès à l'API

- [x] Création de `assets/js/api.js`.
- [x] Centralisation des appels `fetch`.
- [x] Envoi automatique des données au format JSON.
- [x] Transmission du cookie de session avec `credentials: 'same-origin'`.
- [x] Lecture centralisée des réponses JSON et des erreurs HTTP.
- [x] Redirection vers la connexion lorsqu'une session protégée expire.

### Connexion et déconnexion

- [x] Remplacement de la simulation du login par un POST réel vers `/api/auth/login`.
- [x] Envoi des propriétés `nom` et `mdp` au backend.
- [x] Stockage de `nom`, `name`, `email` et `role` dans `sessionStorage`.
- [x] Suppression du choix manuel du rôle sur le formulaire de connexion.
- [x] Utilisation du rôle fourni et validé par le backend.
- [x] Déconnexion côté frontend et invalidation de la session Spring via `POST /logout`.

### Tableaux et formulaires connectés

- [x] Chargement des livreurs avec `GET /api/livreurs`.
- [x] Ajout, modification et suppression des livreurs avec POST, PUT et DELETE.
- [x] Chargement des fournitures avec `GET /api/fournitures`.
- [x] Ajout, modification et suppression des fournitures avec POST, PUT et DELETE.
- [x] Chargement des utilisateurs avec `GET /api/utilisateurs`.
- [x] Ajout, modification et suppression des utilisateurs avec POST, PUT et DELETE.
- [x] Conservation des fonctions de recherche, filtre, tri, pagination et export.
- [x] Gestion visuelle des erreurs réseau et des sessions expirées.

## Refactorisation HTML/JavaScript

### Objectif appliqué

La majorité de la structure visuelle est maintenant écrite directement dans les fichiers HTML. Le JavaScript est limité aux responsabilités dynamiques : collecte des données, appels backend, événements, remplissage des lignes et mise à jour des champs.

### Pages concernées

- [x] `pages/login/index.html`
- [x] `pages/admin/livreurs/index.html`
- [x] `pages/admin/fournitures/index.html`
- [x] `pages/admin/utilisateurs/index.html`

### Éléments déplacés dans les fichiers HTML

- [x] Shell principal de l'application.
- [x] Barre latérale de navigation.
- [x] Barre supérieure.
- [x] Titres et sections des pages.
- [x] Barres de recherche et filtres.
- [x] Structures `<table>`, `<thead>` et `<tbody>`.
- [x] Formulaires d'ajout et de modification.
- [x] Fenêtres de confirmation de suppression.
- [x] Fenêtre de confirmation de déconnexion.
- [x] Fenêtre « mot de passe oublié » du login.
- [x] Petits templates HTML des boutons d'action des lignes.
- [x] Harmonisation des icônes SVG des pages Livreurs, Fournitures et Utilisateurs.

### Responsabilités restantes du JavaScript

- Récupérer les données depuis l'API.
- Créer les lignes correspondant aux données reçues.
- Gérer la recherche, les filtres, le tri et la pagination.
- Remplir les formulaires lors d'une modification.
- Envoyer les requêtes POST, PUT et DELETE.
- Afficher ou fermer les modales déjà présentes dans le HTML.
- Mettre à jour les messages et les états des boutons.

Deux helpers ciblés ont été ajoutés :

- `static-ui.js` : interactions du shell et des modales statiques ;
- `static-datatable.js` : affichage des lignes, filtres, tri et pagination à partir des données.

Les autres pages du template n'ont pas été refactorisées et continuent d'utiliser leurs composants historiques.

## Tests et validations effectués

- [x] Compilation Maven réussie.
- [x] Connexion réelle à PostgreSQL 15.15.
- [x] Création et mise à jour des tables par Hibernate.
- [x] Vérification de la création des tables `client`, `employee`, `fourniture`, `livreur` et `utilisateur`.
- [x] Vérification de la clé étrangère entre `utilisateur` et `employee`.
- [x] Vérification du login avec un mot de passe BCrypt.
- [x] Vérification de la création et de la réutilisation de la session.
- [x] Vérification du cycle CRUD complet : POST → GET → PUT → DELETE.
- [x] Vérification de la déconnexion et du refus d'accès après invalidation de la session.
- [x] Vérification qu'une page publique retourne HTTP 200.
- [x] Vérification qu'une route protégée retourne HTTP 403 sans session.
- [x] Vérification que les tableaux et formulaires sont réellement présents dans les fichiers HTML servis.
- [x] Vérification syntaxique des scripts avec `node --check`.
- [x] Vérification du diff avec `git diff --check`.

### Résultat actuel des tests

```text
Tests exécutés : 4
Échecs : 0
Erreurs : 0
Résultat Maven : BUILD SUCCESS
```

## Organisation architecturale obtenue

```text
controller  → gestion HTTP et réponses REST
dto         → contrats d'entrée et de sortie de l'API
model       → entités persistées par JPA
repository  → accès aux données PostgreSQL
service     → chargement des utilisateurs pour Spring Security
config      → sécurité et amorçage optionnel du premier administrateur
static      → HTML, CSS et JavaScript du frontend
```

## Points à traiter ultérieurement

- [ ] Réactiver la protection CSRF avant une mise en production.
- [ ] Remplacer les identifiants PostgreSQL locaux par des variables d'environnement en production.
- [ ] Ajouter un véritable endpoint d'import Excel pour les fournitures.
- [ ] Ajouter des validations métier plus détaillées sur les DTO.
- [ ] Ajouter une gestion globale des erreurs de contraintes PostgreSQL, par exemple les noms d'utilisateur dupliqués.
- [ ] Envisager des migrations versionnées avec Flyway ou Liquibase à la place de `ddl-auto=update` pour la production.

## Commandes utiles

Lancer les tests :

```powershell
mvn test
```

Lancer l'application :

```powershell
mvn spring-boot:run
```

Vérifier la syntaxe d'un script JavaScript :

```powershell
node --check chemin/vers/script.js
```
