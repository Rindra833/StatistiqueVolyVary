# Voly-Vary — Travaux effectués

Dernière mise à jour : 11 juillet 2026

## Vue d’ensemble

| Chantier | État |
|---|---|
| Configuration PostgreSQL et JPA | ✅ Terminé |
| Authentification Spring Security par formulaire et session | ✅ Terminé |
| Migration des pages utiles vers JSP/JSTL | ✅ Terminé |
| CRUD MVC avec formulaires HTML et modèle PRG | ✅ Terminé |
| Simplification du JavaScript | ✅ Terminé |
| Module statistique JSP/MVC | ✅ Terminé |
| Manuel de préparation à la soutenance | ✅ Terminé |
| Tests MVC, sécurité, PostgreSQL et rendu JSP réel | ✅ Terminé |
| Génération de l’archive WAR | ✅ Terminée |

## Périmètre de la migration JSP

Conformément aux instructions du chef de projet, seules les pages utiles au travail actuel ont été migrées :

- connexion ;
- livreurs ;
- fournitures ;
- utilisateurs ;
- page d’accès refusé.

Les autres pages historiques du template n’ont pas été modifiées. Les anciennes URL des quatre pages concernées restent acceptées et redirigent vers les contrôleurs MVC afin de ne pas casser les liens existants.

## Architecture actuelle

L’application utilise désormais Spring Boot MVC avec des JSP protégées dans `WEB-INF`. Le navigateur ne reçoit plus une page vide que JavaScript doit construire. Le contrôleur charge les données, les place dans un `Model`, puis JSP et JSTL produisent directement le HTML sur le serveur.

```text
Navigateur
    ↓ requête GET ou formulaire POST
Contrôleur MVC
    ↓
Service métier
    ↓
Repository JPA
    ↓
PostgreSQL

Contrôleur MVC
    ↓ Model
JSP + JSTL
    ↓ HTML complet
Navigateur
```

Organisation principale :

```text
src/main/java/com/volyVary/
├── config/          sécurité Spring
├── controller/      contrôleurs MVC
├── model/           entités JPA
├── repository/      accès PostgreSQL
└── service/         logique métier

src/main/webapp/WEB-INF/jsp/
├── connexion.jsp
├── acces-refuse.jsp
├── fragments/
├── livreurs/
├── fournitures/
└── utilisateurs/

src/main/resources/static/assets/
├── css/jsp.css
└── js/jsp-tableau.js
```

## Configuration JSP et déploiement WAR

- [x] Passage du projet Maven au packaging `war`.
- [x] Ajout de Tomcat Jasper pour compiler les JSP.
- [x] Ajout de JSTL Jakarta pour les boucles, conditions et échappements dans les vues.
- [x] Ajout de Tomcat en dépendance `provided` pour permettre un déploiement externe.
- [x] Configuration du préfixe `/WEB-INF/jsp/` et du suffixe `.jsp`.
- [x] Adaptation de `Application` avec `SpringBootServletInitializer`.
- [x] Vérification de la génération de `target/demo-0.0.1-SNAPSHOT.war`.

Les JSP se trouvent dans `WEB-INF/jsp` : elles ne sont pas accessibles directement par URL et doivent obligatoirement passer par un contrôleur Spring MVC.

## Rendu côté serveur

### Connexion

- [x] Création de `connexion.jsp`.
- [x] Formulaire HTML classique envoyé vers `POST /connexion`.
- [x] Champs attendus par Spring Security : `nom` et `mdp`.
- [x] Affichage côté serveur des messages d’échec et de déconnexion.
- [x] Suppression de l’ancien appel JavaScript vers `/api/auth/login`.

### Livreurs

- [x] Liste produite par JSTL à partir du `Model`.
- [x] Recherche et filtres exécutés côté serveur.
- [x] Formulaire JSP commun à la création et à la modification.
- [x] Création et modification avec formulaire `POST`.
- [x] Suppression avec formulaire `POST` protégé par CSRF.

### Fournitures

- [x] Liste produite par JSTL à partir du `Model`.
- [x] Recherche et filtre de catégorie côté serveur.
- [x] Formulaire JSP de création et de modification.
- [x] Création, modification et suppression via formulaires HTML classiques.

### Utilisateurs

- [x] Liste produite par JSTL à partir du `Model`.
- [x] Recherche et filtre de rôle côté serveur.
- [x] Formulaire JSP de création et de modification.
- [x] Association optionnelle avec un employé.
- [x] Encodage BCrypt du mot de passe à la création.
- [x] Conservation du mot de passe actuel si le champ reste vide pendant une modification.
- [x] Suppression via formulaire `POST` protégé par CSRF.

## Modèle POST → redirection → GET

Toutes les écritures des modules migrés suivent le modèle PRG demandé :

1. le navigateur envoie un formulaire `POST` ;
2. le contrôleur valide et enregistre par l’intermédiaire du service ;
3. le contrôleur retourne une redirection ;
4. le navigateur effectue un nouveau `GET` sur la liste ;
5. un message de succès est affiché avec les attributs flash.

Ce modèle empêche une nouvelle insertion accidentelle lorsque l’utilisateur actualise la page.

## Module statistique

Le module statistique est une page consultative rendue côté serveur. Il ne copie aucune donnée dans une table de résultat : `StatistiqueService` exécute des agrégations en lecture seule sur les tables alimentées par les autres modules.

### Indicateurs disponibles

- [x] Transactions de fournitures : nombre, quantité et montant par type de transaction.
- [x] Collectes de paddy : lots reçus, quantité, coût, humidité moyenne et réduction estimée.
- [x] Application de la réduction de 20 % lorsque l’humidité dépasse 15 %.
- [x] Paddy transformé : lots, quantité consommée, coût et quantité de produits obtenus.
- [x] Répartition réelle entre riz blanc, akofom-bary, tofom-bary et vary madinika.
- [x] Statistique globale : transactions, chiffre d’affaires, paddy collecté et paddy transformé.
- [x] Évolution mensuelle des trois modules sur une durée maximale de douze mois.
- [x] Filtrage inclusif entre une date de début et une date de fin.
- [x] Affichage automatique du mois actuel lorsque les deux dates sont absentes.
- [x] Validation des périodes incomplètes ou inversées avec message utilisateur et repli sûr.

### Tables compatibles avec les autres modules

Les noms proviennent des schémas de `instructions.pdf` :

| Table | Colonnes utilisées |
|---|---|
| `type_transaction` | `id`, `libelle` |
| `transaction` | `id`, `id_client`, `id_type`, `reference`, `date` |
| `detail_transaction` | `id`, `id_transaction`, `id_fourniture`, `quantite` |
| `lot_paddy` | `id`, `reference`, `quantite`, `taux_humidite`, `date`, `prix_collecte` |
| `lot_paddy_transforme` | `id`, `reference`, `quantite`, `date`, `prix_transformation` |
| `produit` | `id`, `nom`, `prix_unitaire` |
| `detail_lot_transforme` | `id`, `id_lot_transforme`, `id_lot`, `id_produit`, `quantite`, `date` |

Les relations vers `client`, `fourniture`, les transactions, les lots et les produits sont matérialisées par des clés étrangères JPA. Tous les identifiants utilisent `Integer`.

### Affichage

- [x] Nouvelle vue `WEB-INF/jsp/statistiques/index.jsp`.
- [x] Valeurs, tableaux et barres d’évolution écrits dans le HTML par JSP/JSTL.
- [x] Aucun appel `fetch` et aucun script de génération HTML.
- [x] Chart.js installé localement avec WebJars et limité au dessin d’un graphique à partir du tableau JSP.
- [x] Soumission automatique du filtre de période avec un bouton de secours lorsque JavaScript est désactivé.
- [x] Ancienne page statistique statique et ses données simulées supprimées.
- [x] Route protégée `GET /admin/statistiques` et alias de compatibilité pour l’ancienne URL.

## Séparation des responsabilités

- [x] Les contrôleurs gèrent les routes, le `Model`, les formulaires et les redirections.
- [x] Les services centralisent les recherches, filtres et opérations CRUD.
- [x] Les repositories restent responsables de l’accès aux données.
- [x] Les JSP contiennent la structure HTML et les expressions JSTL.
- [x] Le JavaScript ne porte aucune logique de persistance et ne construit aucune page.
- [x] Les variables ajoutées dans les services et contrôleurs portent des noms français.
- [x] Chaque fonction ajoutée contient un commentaire pédagogique détaillant son rôle, ses entrées et son résultat.

## JavaScript conservé

Deux scripts d’aide sont utilisés par les pages JSP.

`assets/js/jsp-tableau.js` est limité à deux aides facultatives :

- exporter en CSV le tableau HTML déjà rendu par le serveur ;
- demander l’impression de la page déjà rendue.

Il ne récupère pas les données, ne génère pas les lignes des tableaux, ne crée pas les formulaires et n’effectue aucune opération CRUD. La confirmation de suppression est une petite interaction attachée au formulaire HTML existant.

`assets/js/statistiques-graphiques.js` lit les lignes du tableau de transactions déjà rendu par JSP puis transmet uniquement leurs valeurs à Chart.js. Le type du diagramme peut être changé pour la démonstration avec une seule constante :

```javascript
const TYPE_GRAPHIQUE_TRANSACTIONS = 'bar'; // bar, pie ou doughnut
```

Chart.js est embarqué dans le WAR avec le WebJar Maven `org.webjars.npm:chart.js:4.4.1` : aucune connexion Internet n’est nécessaire pendant la présentation.

Les anciens scripts `api.js`, `static-ui.js`, `static-datatable.js` et les scripts spécifiques de génération des pages migrées ont été retirés.

### Données statistiques de démonstration

- [x] Six mois de transactions, collectes et transformations cohérentes.
- [x] Références préfixées par `DEMO-STAT-` pour éviter toute confusion avec les données métier.
- [x] Initialisation idempotente : les lignes ne sont pas recréées à chaque démarrage.
- [x] Activation contrôlée par `app.statistiques.donnees-demonstration=true`.
- [x] Désactivation explicite pendant les tests automatisés.
- [x] Script de nettoyage disponible dans `database/nettoyer_donnees_demo_statistiques.sql`.

## Sécurité

- [x] Chargement des comptes par `CustomUserDetailsService` et `UtilisateurRepository`.
- [x] Mots de passe encodés avec BCrypt.
- [x] Authentification par formulaire et session Spring Security.
- [x] Routes publiques limitées à la connexion et aux ressources statiques nécessaires.
- [x] Routes d’administration réservées au rôle `Administrateur`.
- [x] Protection CSRF active sur tous les formulaires `POST`.
- [x] Jeton CSRF inclus dans les formulaires de connexion, d’enregistrement, de suppression et de déconnexion.
- [x] Déconnexion par `POST /deconnexion` avec invalidation de session.
- [x] Page JSP dédiée au refus d’accès.
- [x] Autorisation des dispatchers internes `FORWARD` et `ERROR` nécessaires au rendu JSP.

### Amorçage du premier administrateur

Aucun mot de passe par défaut n’est enregistré dans le dépôt. Le premier administrateur peut être créé avec des variables d’environnement :

```powershell
$env:VOLYVARY_ADMIN_NOM="admin"
$env:VOLYVARY_ADMIN_PASSWORD="un-mot-de-passe-solide"
$env:VOLYVARY_ADMIN_ROLE="Administrateur"
mvn spring-boot:run
```

Le compte est créé uniquement si son nom n’existe pas déjà.

## Base de données

- [x] Connexion à PostgreSQL sur la base `volyvary_db`.
- [x] Gestion du schéma avec `spring.jpa.hibernate.ddl-auto=update`.
- [x] Repositories disponibles pour `Utilisateur`, `Livreur`, `Fourniture`, `Employee` et `Client`.
- [x] Uniformisation de tous les identifiants avec le type Java `Integer` dans les entités, repositories, services et contrôleurs.
- [x] Conservation de la séparation entre les données métier `Employee` et le compte système `Utilisateur`.
- [x] Conservation de la relation optionnelle entre `Utilisateur` et `Employee`.
- [x] Désactivation de `spring.jpa.open-in-view` afin de garder la persistance hors de la vue.

## Nettoyage effectué

- [x] Retrait des anciens contrôleurs REST de connexion, livreurs, fournitures et utilisateurs, devenus inutiles après la migration MVC.
- [x] Retrait des DTO uniquement utilisés par ces anciens endpoints.
- [x] Retrait des anciens fichiers HTML et JavaScript des quatre pages migrées.
- [x] Conservation des modules non concernés par la demande.
- [x] Conservation temporaire des contrôleurs REST `Client` et `Employee`, car leurs pages n’étaient pas dans le périmètre de cette migration.
- [x] Vérification qu’aucun fichier ne référence encore les helpers JavaScript supprimés.

## Tests et validations

- [x] Démarrage du contexte Spring Boot avec PostgreSQL 15.15.
- [x] Vérification de l’authentification par formulaire et de la session.
- [x] Vérification de l’accès protégé aux pages d’administration.
- [x] Vérification du refus d’un formulaire sans jeton CSRF.
- [x] Vérification du cycle création → redirection → affichage → modification → suppression.
- [x] Vérification de la déconnexion.
- [x] Démarrage d’un vrai serveur Tomcat sur un port aléatoire pendant les tests.
- [x] Compilation et rendu réels des JSP par Jasper.
- [x] Vérification des listes et formulaires JSP pour livreurs, fournitures et utilisateurs.
- [x] Vérification syntaxique du JavaScript restant avec `node --check`.
- [x] Correction du style de connexion en déplaçant sa feuille dans les assets publics autorisés par Spring Security.
- [x] Encodage UTF-8 forcé pour les réponses HTTP et les fragments JSP afin d’éviter les caractères corrompus.
- [x] Test d’intégration statistique avec transaction, collectes, réduction d’humidité et transformation 65/20/10/5.
- [x] Annulation automatique des données de test afin de ne pas polluer la base partagée.
- [x] Vérification HTTP de la bibliothèque Chart.js locale.
- [x] Génération réussie de l’archive WAR.

Résultat final :

```text
Tests exécutés : 7
Échecs : 0
Erreurs : 0
Résultat Maven : BUILD SUCCESS
Archive : target/demo-0.0.1-SNAPSHOT.war
```

## Routes MVC utiles

| Fonction | Méthode et route |
|---|---|
| Afficher la connexion | `GET /connexion` |
| Traiter la connexion | `POST /connexion` |
| Se déconnecter | `POST /deconnexion` |
| Lister les livreurs | `GET /admin/livreurs` |
| Ajouter/modifier un livreur | `POST /admin/livreurs/enregistrer` |
| Lister les fournitures | `GET /admin/fournitures` |
| Ajouter/modifier une fourniture | `POST /admin/fournitures/enregistrer` |
| Lister les utilisateurs | `GET /admin/utilisateurs` |
| Ajouter/modifier un utilisateur | `POST /admin/utilisateurs/enregistrer` |
| Afficher les statistiques | `GET /admin/statistiques` |

## Commandes utiles

Lancer l’application :

```powershell
mvn spring-boot:run
```

Lancer tous les tests :

```powershell
mvn test
```

Générer le WAR :

```powershell
mvn package
```

Vérifier le petit script restant :

```powershell
node --check src/main/resources/static/assets/js/jsp-tableau.js
```

## Manuel pédagogique

Le fichier `MANUEL_SOUTENANCE.md` documente l’architecture, les calculs, la sécurité, les fichiers à modifier selon une demande du professeur, les scénarios pratiques, les erreurs fréquentes et l’ordre conseillé pour étudier le projet.

## Améliorations possibles hors périmètre

- [ ] Remplacer les identifiants PostgreSQL locaux par des variables d’environnement pour le déploiement.
- [ ] Ajouter Bean Validation et afficher les erreurs de champs dans les JSP.
- [ ] Gérer globalement les conflits de contraintes PostgreSQL, par exemple un nom d’utilisateur dupliqué.
- [ ] Utiliser Flyway ou Liquibase à la place de `ddl-auto=update` en production.
- [ ] Migrer les autres pages historiques vers JSP seulement si elles entrent dans un futur périmètre fonctionnel.
