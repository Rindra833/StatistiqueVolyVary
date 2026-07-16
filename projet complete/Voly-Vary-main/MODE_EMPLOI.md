# Mode d'emploi de Voly-Vary — Guide de soutenance

## 1. Présentation de l'application en 30 secondes

**Voly-Vary** est une application web de gestion destinée à une organisation travaillant autour de la filière rizicole. Elle centralise :

- les achats de paddy auprès des producteurs ;
- la transformation du paddy en produits finis et sous-produits ;
- la distribution des produits transformés ;
- les transactions portant sur les fournitures ;
- les clients et les données de référence nécessaires aux opérations ;
- les statistiques globales et détaillées de l'activité.

L'application utilise **Spring Boot MVC**, des vues **JSP/JSTL**, **PostgreSQL** et **Spring Security**. Les pages sont rendues côté serveur. JavaScript sert seulement aux aides d'interface et aux graphiques, pas à construire la structure principale des pages.

## 2. Contexte et objectif

Dans une activité rizicole, les informations sont réparties entre plusieurs étapes : achat du paddy, transformation, vente ou distribution et suivi des résultats. Voly-Vary rassemble ces opérations dans une seule base de données afin de :

- conserver un historique fiable ;
- éviter les ressaisies inutiles ;
- contrôler les accès selon la fonction de l'utilisateur ;
- suivre les quantités et les montants ;
- produire automatiquement des indicateurs statistiques.

## 3. Circuit métier global

```text
Référentiels administratifs
  ├── Catégories ──> Fournitures ──> Transactions de fournitures
  ├── Clients ─────────────────────> Transactions / Collectes / Distributions
  ├── Livreurs ────────────────────> Distributions
  └── Lieux de livraison ──────────> Distributions

Producteur / Client
  └── Collecte de paddy
        └── Lot de paddy
              └── Transformation
                    └── Produits transformés
                          └── Distribution au client

Transactions + Collectes + Transformations
  └── Tableau de bord statistique
```

Les statistiques ne sont donc pas saisies séparément : elles sont calculées à partir des opérations enregistrées dans les autres modules.

## 4. Architecture technique résumée

| Couche | Responsabilité |
|---|---|
| Modèle | Représente les tables et les relations de la base PostgreSQL. |
| Repository | Lit et enregistre les entités avec Spring Data JPA. |
| Service | Contient les règles métier et les calculs. |
| Controller | Reçoit les requêtes, prépare le `Model` et choisit la vue. |
| JSP/JSTL | Produit le HTML envoyé au navigateur. |
| JavaScript | Apporte de petites interactions et affiche les graphiques. |
| Spring Security | Gère la connexion, la session, les rôles et les autorisations. |

Les formulaires suivent principalement le modèle **POST → redirection → GET** : le POST traite et enregistre l'opération, puis redirige vers une page GET. Cela évite qu'un rafraîchissement du navigateur répète une écriture.

## 5. Démarrer l'application

### Prérequis

- Java installé ;
- PostgreSQL démarré ;
- base `volyvary_db` disponible ;
- identifiants PostgreSQL correctement renseignés dans `src/main/resources/application.properties`.

### Commandes sous PowerShell

Depuis le dossier `projet complete/Voly-Vary-main` :

```powershell
.\mvnw.cmd spring-boot:run
```

Ouvrir ensuite :

```text
http://localhost:8080/connexion
```

Pour arrêter l'application, utiliser `Ctrl + C` dans le terminal.

## 6. Connexion et profils utilisateurs

1. Saisir le nom d'utilisateur et le mot de passe.
2. Cliquer sur **Se connecter**.
3. L'application ouvre la page adaptée au rôle.
4. Utiliser **Déconnexion** à la fin de la session.

| Profil | Accès principal |
|---|---|
| Administrateur | Tous les modules, les statistiques et les référentiels administratifs. |
| Responsable Statistiques | Tableau de bord statistique. |
| Responsable Transaction | Création et consultation des transactions, gestion des clients. |
| Responsable Collecte | Création et suivi des collectes, gestion des clients. |
| Responsable Transformation | Création et consultation des transformations. |
| Responsable Distribution | Création et suivi des distributions, gestion des clients. |

Le menu latéral s'adapte au profil connecté. La sécurité est également contrôlée côté serveur : saisir directement l'adresse d'une page non autorisée provoque un refus d'accès.

## 7. Mode d'emploi par module

### 7.1 Clients

**But :** conserver l'identité des personnes ou organisations liées aux transactions, collectes et distributions.

**Utilisation :**

1. Ouvrir **Clients**.
2. Cliquer sur **Ajouter un client**.
3. Renseigner la référence, le nom, le prénom et le téléphone.
4. Enregistrer.
5. Utiliser la référence du client dans les opérations métier.

La référence identifie le client. Lors de certaines opérations, une référence inconnue accompagnée des informations du client peut aussi entraîner sa création automatique.

La suppression d'un client déjà utilisé peut être refusée afin de préserver l'historique des opérations.

### 7.2 Catégories de fournitures

**But :** classer les fournitures dans un référentiel commun.

**Utilisation :**

1. Se connecter comme administrateur.
2. Ouvrir **Catégories**.
3. Cliquer sur **Ajouter une catégorie**.
4. Saisir un libellé unique, puis enregistrer.
5. Utiliser cette catégorie lors de la création d'une fourniture.

Une catégorie ne peut pas être supprimée tant qu'une fourniture l'utilise. Deux libellés identiques, même avec une casse différente, sont refusés.

### 7.3 Fournitures

**But :** gérer le catalogue des fournitures utilisées par le module de transactions.

**Prérequis :** créer au moins une catégorie.

**Utilisation :**

1. Ouvrir **Administration → Fournitures**.
2. Cliquer sur **Ajouter une fourniture**.
3. Renseigner la référence, la catégorie et le prix unitaire.
4. Enregistrer.
5. Rechercher ou filtrer les fournitures depuis la liste.

La liste permet également la modification, la suppression et l'export. Une fourniture déjà liée à une opération doit être conservée pour ne pas casser l'historique.

### 7.4 Transactions de fournitures

**But :** enregistrer une vente directe, une vente à crédit ou une location de fournitures.

**Prérequis :** disposer d'au moins une catégorie et d'une fourniture.

**Utilisation :**

1. Ouvrir **Nouvelle transaction**.
2. Choisir le type de transaction et la date.
3. Renseigner la référence du client et ses informations.
4. Choisir une catégorie, puis cliquer sur **Charger**.
5. Ajouter une ou plusieurs fournitures et saisir leurs quantités.
6. Cliquer sur **Faire la transaction** pour afficher le récapitulatif.
7. Vérifier le montant, puis confirmer ou annuler.

Le montant total correspond à la somme de `prix unitaire × quantité` pour toutes les lignes. La page **Voir les transactions** permet de filtrer par référence, client, type et période, d'afficher les détails et d'exporter les résultats.

### 7.5 Collectes de paddy

**But :** enregistrer l'achat de paddy auprès d'un producteur et créer le lot qui pourra être transformé.

**Utilisation :**

1. Ouvrir **Nouvelle collecte**.
2. Renseigner la date, le client ou producteur, la quantité en kilogrammes, le taux d'humidité et le prix unitaire de base.
3. Cliquer sur **Calculer et valider**.
4. Vérifier le prix corrigé et le montant total.
5. Cliquer sur **Confirmer l'achat**.
6. Depuis le détail, choisir **Payer** pour valider la collecte ou **Annuler**.

La réduction dépend du taux d'humidité :

```text
Prix corrigé = prix de base × (1 - taux de réduction)
Montant total = quantité × prix corrigé
```

Les listes permettent de consulter les collectes en attente, validées ou toutes les collectes, puis d'imprimer la facture ou d'exporter les données.

### 7.6 Transformations de paddy

**But :** consommer du paddy disponible et produire du riz ainsi que ses sous-produits.

**Prérequis :** disposer de lots de paddy disponibles.

**Utilisation :**

1. Ouvrir **Nouvelle transformation**.
2. Vérifier les lots et la quantité de paddy disponibles.
3. Saisir la date, la quantité à transformer et le coût de transformation par kilogramme.
4. Cliquer sur **Transformer**.
5. Consulter le résultat dans **Voir les transformations**.

L'application refuse une quantité nulle, négative ou supérieure au stock disponible. Une transformation peut consommer plusieurs lots. Elle calcule son coût et génère automatiquement les quantités de produits selon leurs rendements configurés.

Les détails indiquent les lots sources et les produits obtenus. Les listes et fiches peuvent être exportées en PDF.

### 7.7 Distributions

**But :** enregistrer la distribution de produits transformés vers un client.

**Prérequis :** disposer de produits, d'un livreur et d'un lieu de livraison.

**Utilisation :**

1. Ouvrir **Nouvelle distribution**.
2. Renseigner la référence et les informations du client.
3. Ajouter les produits et leurs quantités.
4. Choisir le lieu de livraison et le livreur.
5. Enregistrer la distribution.
6. Consulter la facture et le détail.
7. Marquer la distribution comme **Terminée** ou **Annulée** selon le résultat.

La page de liste permet de rechercher, filtrer par statut, trier, consulter et exporter les distributions.

### 7.8 Livreurs et lieux de livraison

Ces deux référentiels sont utilisés par la distribution.

**Livreur :**

1. Ouvrir **Administration → Livreurs**.
2. Ajouter le matricule du véhicule.
3. Enregistrer.

**Lieu de livraison :**

1. Ouvrir **Administration → Lieux de livraison**.
2. Ajouter le nom du lieu.
3. Enregistrer.

Un élément déjà utilisé par une distribution ne doit pas être supprimé, car il appartient à l'historique de cette opération.

### 7.9 Utilisateurs

**But :** créer les comptes et attribuer les responsabilités.

**Utilisation :**

1. Ouvrir **Administration → Utilisateurs**.
2. Cliquer sur **Ajouter un utilisateur**.
3. Saisir le nom d'utilisateur, le mot de passe et le rôle.
4. Associer éventuellement le compte à un employé.
5. Enregistrer.

Le mot de passe est obligatoire à la création. Lors d'une modification, le laisser vide permet de conserver l'ancien mot de passe. Les mots de passe sont stockés sous forme chiffrée avec BCrypt.

### 7.10 Statistiques

**But :** synthétiser les données réellement enregistrées dans les transactions, les collectes et les transformations.

**Utilisation :**

1. Ouvrir **Tableau de bord** avec un profil autorisé.
2. Sans saisir de dates, consulter automatiquement les données du mois actuel.
3. Pour une période personnalisée, saisir les dates **Du** et **Au**.
4. Cliquer sur **Filtrer**.
5. Utiliser **Mois actuel** pour revenir rapidement à la période par défaut.

Les deux dates sont obligatoires pour un filtre personnalisé et la date de début doit précéder ou être égale à la date de fin.

Le tableau de bord comporte quatre ensembles d'informations :

1. **Statistique globale :** transactions, chiffre d'affaires, paddy collecté et paddy transformé.
2. **Transactions de fournitures :** nombre, quantité et montant par type de transaction.
3. **Collectes de paddy :** lots, quantité, coût, humidité moyenne et réductions appliquées.
4. **Transformations :** lots transformés, quantité consommée, coût et répartition des produits obtenus.

L'évolution mensuelle compare les transactions, collectes et transformations sur un maximum de douze mois. Les graphiques utilisent Chart.js uniquement pour la représentation visuelle ; les valeurs sont calculées côté serveur depuis PostgreSQL.

## 8. Ordre conseillé pour une démonstration

Pour éviter les listes vides ou les prérequis manquants, effectuer la démonstration dans cet ordre :

1. Se connecter comme administrateur et montrer le menu adapté au rôle.
2. Créer une catégorie, puis une fourniture.
3. Créer un client.
4. Enregistrer et confirmer une transaction de fourniture.
5. Créer une collecte de paddy, puis la payer.
6. Transformer une partie du paddy disponible.
7. Créer un livreur et un lieu de livraison si nécessaire.
8. Enregistrer une distribution des produits obtenus.
9. Ouvrir les statistiques et filtrer sur la période de démonstration.
10. Montrer que les nouveaux nombres et montants apparaissent dans le tableau de bord.

## 9. Contrôles métier importants

| Situation | Comportement attendu |
|---|---|
| Utilisateur sans le bon rôle | Accès refusé côté serveur. |
| Catégorie déjà existante | Création refusée. |
| Catégorie encore utilisée | Suppression refusée. |
| Client ou référentiel lié à une opération | Suppression susceptible d'être refusée pour préserver l'historique. |
| Quantité ou prix non positif | Opération refusée. |
| Humidité hors de 0 à 100 % | Collecte refusée. |
| Transformation supérieure au paddy disponible | Transformation refusée sans écriture partielle. |
| Une seule date statistique renseignée | Filtre refusé avec un message explicatif. |
| Début statistique après la fin | Filtre refusé. |

## 10. Réponses courtes pour la soutenance

### Pourquoi utiliser JSP/JSTL ?

Pour produire les pages côté serveur avec Spring MVC. Le contrôleur fournit les données au `Model`, puis la JSP construit le HTML. Cela garde JavaScript limité aux interactions légères.

### D'où viennent les chiffres du tableau de bord ?

Ils proviennent directement des tables métier : transactions, détails de transaction, lots de paddy et lots transformés. Le module statistique ne crée pas une seconde copie des opérations.

### Pourquoi séparer catégorie et fourniture ?

Une catégorie est une donnée de référence réutilisable par plusieurs fournitures. Cette séparation évite de répéter le même texte et garantit des filtres cohérents.

### Pourquoi utiliser une couche service ?

Elle centralise les règles métier, comme le calcul du prix corrigé d'une collecte, la vérification du stock de paddy ou le calcul des statistiques. Les contrôleurs restent ainsi concentrés sur la navigation et les formulaires.

### Comment la sécurité est-elle assurée ?

Spring Security authentifie l'utilisateur, conserve sa session, contrôle son rôle et protège les URL. Le menu masque les fonctions non autorisées, mais la protection principale reste côté serveur.

### Pourquoi le modèle POST → redirection → GET ?

Il évite de soumettre deux fois une opération lorsque l'utilisateur actualise la page après un enregistrement.

### Pourquoi utiliser JavaScript dans les statistiques ?

Le serveur calcule et transmet les données. Chart.js les transforme uniquement en graphiques. Le type d'un graphique peut donc être modifié sans changer les règles métier ni les requêtes de calcul.

## 11. Diagnostic rapide pendant la démonstration

- **Une liste déroulante est vide :** créer d'abord la donnée de référence correspondante.
- **Aucun paddy n'est transformable :** enregistrer puis valider une collecte.
- **Aucun produit n'est distribuable :** effectuer d'abord une transformation.
- **Les statistiques sont à zéro :** vérifier que la période contient les dates des opérations.
- **Une suppression échoue :** l'élément est probablement déjà référencé par une opération.
- **Une page est refusée :** vérifier le rôle du compte connecté.
- **L'application ne démarre pas :** vérifier Java, PostgreSQL et les paramètres de connexion à la base.

---

**Phrase de conclusion conseillée :** Voly-Vary assure la continuité entre les données de référence, les opérations de la chaîne rizicole et leur analyse, tout en séparant les responsabilités métier et les droits des utilisateurs.
