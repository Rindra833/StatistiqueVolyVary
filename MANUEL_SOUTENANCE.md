# Manuel de compréhension et de modification — Voly-Vary

Ce manuel sert à deux choses : comprendre rapidement l’application et retrouver le bon fichier lorsqu’une petite modification est demandée pendant la soutenance.

## 1. Résumé à retenir en une minute

Voly-Vary est une application Spring Boot organisée en couches :

```text
Navigateur
    ↓ requête HTTP
Controller MVC
    ↓ appelle
Service
    ↓ utilise
Repository JPA ou JDBC
    ↓
PostgreSQL

Controller MVC
    ↓ ajoute les résultats dans Model
JSP + JSTL
    ↓ produit le HTML
Navigateur
```

Rôle de chaque couche :

| Couche | Responsabilité | Ne doit pas faire |
|---|---|---|
| `controller` | Lire la requête, remplir `Model`, choisir une JSP ou une redirection | Écrire les calculs métier |
| `service` | Appliquer les règles métier et préparer les résultats | Construire du HTML |
| `repository` | Lire et écrire les entités | Décider de l’affichage |
| `model` | Décrire les tables et relations JPA | Gérer les requêtes HTTP |
| `dto` | Transporter un résultat préparé entre service et vue | Accéder directement à la base |
| JSP | Écrire le HTML et afficher les valeurs du `Model` | Faire les calculs principaux |
| JavaScript | Petites aides et graphiques | Créer toute la page ou réaliser le CRUD |

La séparation importante à expliquer : `Employee` contient les données RH, tandis que `Utilisateur` représente le compte de connexion.

## 2. Les fichiers à connaître en priorité

### Démarrage et configuration

| Besoin | Fichier |
|---|---|
| Démarrage Java et déploiement WAR | `src/main/java/com/volyVary/Application.java` |
| PostgreSQL, JSP, UTF-8 et données de démo | `src/main/resources/application.properties` |
| Dépendances Maven, WAR, JSTL et Chart.js | `pom.xml` |
| Règles d’accès, connexion, déconnexion et CSRF | `src/main/java/com/volyVary/config/SecurityConfig.java` |
| Création facultative du premier administrateur | `src/main/java/com/volyVary/config/InitialAdminConfig.java` |
| Chargement d’un compte pendant la connexion | `src/main/java/com/volyVary/service/CustomUserDetailsService.java` |

### Pages MVC principales

| Module | Contrôleur | Service | JSP |
|---|---|---|---|
| Connexion | `PageController` + Spring Security | `CustomUserDetailsService` | `WEB-INF/jsp/connexion.jsp` |
| Livreurs | `LivreurMvcController` | `LivreurService` | `WEB-INF/jsp/livreurs/` |
| Fournitures | `FournitureMvcController` | `FournitureService` | `WEB-INF/jsp/fournitures/` |
| Utilisateurs | `UtilisateurMvcController` | `UtilisateurService` | `WEB-INF/jsp/utilisateurs/` |
| Statistiques | `StatistiqueMvcController` | `StatistiqueService` | `WEB-INF/jsp/statistiques/index.jsp` |

Fragments partagés :

- `WEB-INF/jsp/fragments/navigation.jspf` : menu latéral ;
- `WEB-INF/jsp/fragments/barre-superieure.jspf` : barre supérieure ;
- `assets/css/jsp.css` : styles complémentaires des pages JSP.

## 3. Comment une requête est traitée

### Exemple : ouvrir les statistiques

1. Le navigateur demande `GET /admin/statistiques?debut=2026-02-01&fin=2026-07-11`.
2. `SecurityConfig` vérifie la session et le rôle `Administrateur`.
3. `StatistiqueMvcController.afficher()` reçoit les deux dates.
4. Le contrôleur appelle `StatistiqueService.calculerTableau(debut, fin)`.
5. Le service valide les dates puis exécute les agrégations SQL.
6. Le service retourne un `TableauStatistique`.
7. Le contrôleur place cet objet dans `Model` sous le nom `tableau`.
8. Spring résout le nom `statistiques/index` vers `/WEB-INF/jsp/statistiques/index.jsp`.
9. JSTL écrit les cartes, tableaux et barres HTML.
10. `statistiques-graphiques.js` lit le tableau de transactions déjà rendu et Chart.js dessine le graphique.

### Exemple : enregistrer un livreur

1. Le formulaire JSP envoie `POST /admin/livreurs/enregistrer` avec un jeton CSRF.
2. `LivreurMvcController.enregistrer()` reçoit les champs.
3. `LivreurService.enregistrer()` crée ou modifie l’entité.
4. `LivreurRepository.save()` écrit dans PostgreSQL.
5. Le contrôleur retourne `redirect:/admin/livreurs`.
6. Le navigateur réalise un nouveau GET.

Ce fonctionnement s’appelle POST → redirection → GET, ou modèle PRG. Il évite un second enregistrement lors d’un rafraîchissement.

## 4. Module statistique en détail

### 4.1 Tables sources

Le module ne possède pas de table `statistique`. Il calcule en lecture seule à partir des tables des autres modules.

| Table | Colonnes importantes | Utilisation |
|---|---|---|
| `type_transaction` | `id`, `libelle` | Directe, crédit, location |
| `transaction` | `id`, `id_client`, `id_type`, `reference`, `date` | En-tête d’une transaction |
| `detail_transaction` | `id_transaction`, `id_fourniture`, `quantite` | Fournitures d’une transaction |
| `fourniture` | `id`, `prix` | Calcul du montant |
| `lot_paddy` | `quantite`, `taux_humidite`, `date`, `prix_collecte` | Statistiques de collecte |
| `lot_paddy_transforme` | `quantite`, `date`, `prix_transformation` | Paddy consommé et coût |
| `produit` | `id`, `nom`, `prix_unitaire` | Catalogue des produits obtenus |
| `detail_lot_transforme` | `id_lot_transforme`, `id_lot`, `id_produit`, `quantite` | Répartition de la production |

Les entités correspondantes sont dans `src/main/java/com/volyVary/model/`.

### 4.2 Objets de résultat

Les records de `dto/statistique` sont des objets immuables destinés à l’affichage :

| Record | Contenu |
|---|---|
| `PeriodeStatistique` | Code, libellé, début, fin |
| `ResumeGlobalStatistique` | Transactions, chiffre d’affaires, collecte, transformation |
| `LigneTransactionStatistique` | Une ligne par type de transaction |
| `StatistiqueCollecte` | Lots, quantité, coût, humidité, réduction |
| `StatistiqueTransformation` | Totaux et répartition des produits |
| `PointMensuelStatistique` | Valeurs d’un mois et largeurs des barres HTML |
| `TableauStatistique` | Objet racine contenant toute la page |

Pourquoi utiliser des records : ils sont courts, immuables et adaptés au transport de résultats qui ne doivent pas être modifiés par la vue.

### 4.3 Méthodes principales de `StatistiqueService`

| Méthode | Rôle | À modifier si… |
|---|---|---|
| `calculerTableau()` | Assemble les quatre parties | On ajoute une nouvelle famille de statistiques |
| `determinerPeriode()` | Valide début/fin et applique le mois actuel | On change la période par défaut |
| `calculerTransactions()` | Nombre, quantité et montant par type | On change un calcul de transaction |
| `calculerCollecte()` | Volume, coût, humidité et réduction | On change la règle d’humidité |
| `calculerTransformation()` | Paddy transformé et produits obtenus | On change la répartition affichée |
| `calculerEvolution()` | Prépare au maximum 12 mois | On change la longueur de l’historique |
| `calculerLargeur()` | Convertit une valeur en largeur de 0 à 100 % | On change les barres HTML |
| `convertirDecimal()` | Uniformise les nombres JDBC | On change l’arrondi monétaire |

### 4.4 Formules utilisées

Transactions :

```text
montant = somme(detail_transaction.quantite × fourniture.prix)
```

Collectes :

```text
quantité totale = somme(lot_paddy.quantite)
coût total = somme(lot_paddy.prix_collecte)
humidité moyenne = moyenne(lot_paddy.taux_humidite)
réduction estimée = prix_collecte × 20 % si taux_humidite > 15
```

Transformations :

```text
paddy transformé = somme(lot_paddy_transforme.quantite)
produit obtenu = somme(detail_lot_transforme.quantite) par produit
pourcentage produit = quantité produit ÷ quantité totale des produits × 100
```

Attention pour une question du professeur : le montant d’une transaction utilise actuellement le prix présent dans `fourniture`. Si ce prix change après une ancienne vente, l’ancien montant change aussi. Une amélioration professionnelle serait de stocker un prix historique dans `detail_transaction`, mais cette colonne doit être coordonnée avec l’équipe du module transaction avant modification.

### 4.5 Filtre entre deux dates

Le formulaire se trouve dans `WEB-INF/jsp/statistiques/index.jsp`.

Le contrôleur reçoit :

```java
LocalDate debut
LocalDate fin
```

Le service applique ces règles :

- les deux dates sont absentes : premier jour du mois actuel jusqu’à aujourd’hui ;
- une seule date est présente : erreur utilisateur puis repli sur le mois actuel ;
- début après fin : erreur utilisateur puis repli sur le mois actuel ;
- deux dates valides : filtre inclusif avec SQL `BETWEEN :debut AND :fin`.

Les totaux couvrent toute la période. L’évolution visuelle est volontairement limitée aux douze derniers mois de cette période.

## 5. Chart.js et JavaScript

Chart.js est installé localement avec le WebJar Maven :

```xml
org.webjars.npm:chart.js:4.4.1
```

Il fonctionne sans Internet. Le fichier concerné est :

```text
src/main/resources/static/assets/js/statistiques-graphiques.js
```

Pour changer le graphique pendant la soutenance :

```javascript
const TYPE_GRAPHIQUE_TRANSACTIONS = 'bar';
```

Valeurs possibles avec le code actuel :

```javascript
'bar'
'pie'
'doughnut'
```

Pourquoi ce JavaScript respecte l’architecture : il ne fait aucun `fetch`, ne construit aucun tableau et n’enregistre rien. Il lit les lignes HTML déjà rendues par JSP puis dessine seulement dans le `<canvas>`.

Autre script :

- `jsp-tableau.js` : export CSV et impression d’un tableau existant.

## 6. Sécurité à connaître

### Connexion

- page : `GET /connexion` ;
- traitement Spring Security : `POST /connexion` ;
- identifiant : champ `nom` ;
- mot de passe : champ `mdp` ;
- réussite : redirection vers `/admin/livreurs` ;
- échec : redirection vers `/connexion?erreur`.

### Déconnexion

- formulaire `POST /deconnexion` ;
- session invalidée ;
- cookie `JSESSIONID` supprimé ;
- redirection vers `/connexion?deconnexion`.

### CSRF

Tous les formulaires POST contiennent :

```jsp
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
```

Si le professeur demande pourquoi un POST retourne 403, vérifier en premier la présence de ce champ.

### Rôles

`Utilisateur.getAuthorities()` transforme par exemple `Administrateur` en `ROLE_Administrateur`. Dans `SecurityConfig`, `hasRole("Administrateur")` ajoute lui-même le préfixe `ROLE_`.

## 7. Données de démonstration

Classe :

```text
config/DonneesDemonstrationStatistiqueConfig.java
```

Activation dans `application.properties` :

```properties
app.statistiques.donnees-demonstration=true
```

Caractéristiques :

- six mois de données ;
- références commençant par `DEMO-STAT-` ;
- insertion transactionnelle ;
- création une seule fois ;
- proportions 65/20/10/5 ;
- types et produits existants réutilisés pour éviter les doublons.

Avant fusion finale :

```properties
app.statistiques.donnees-demonstration=false
```

Nettoyage : exécuter `database/nettoyer_donnees_demo_statistiques.sql` dans PostgreSQL.

Les tests désactivent explicitement cet amorçage et utilisent leurs propres lignes annulées avec `@Transactional`.

## 8. “Le professeur demande X” — où modifier ?

| Demande | Premier fichier à ouvrir | Modification typique |
|---|---|---|
| Passer le diagramme en gâteau | `statistiques-graphiques.js` | Remplacer `'bar'` par `'pie'` |
| Passer le diagramme en anneau | `statistiques-graphiques.js` | Remplacer `'bar'` par `'doughnut'` |
| Changer la couleur du graphique | `statistiques-graphiques.js` | Modifier le tableau `couleurs` |
| Ajouter une carte globale | DTO + `StatistiqueService` + JSP | Ajouter la valeur, la calculer, l’afficher |
| Modifier la réduction de 20 % | `StatistiqueService` | Modifier `TAUX_REDUCTION_HUMIDITE` et le test |
| Modifier le seuil de 15 % | `StatistiqueService.calculerCollecte()` | Modifier la condition SQL et le test |
| Afficher 6 mois au lieu de 12 | `StatistiqueService.calculerEvolution()` | Remplacer `minusMonths(11)` par `minusMonths(5)` |
| Changer la période par défaut | `StatistiqueService.determinerPeriode()` | Modifier les dates utilisées lorsque les paramètres sont null |
| Rendre la date de fin obligatoire côté HTML | JSP statistiques | Ajouter `required` sur l’input |
| Ajouter un filtre de type | Contrôleur + service + JSP | Recevoir le paramètre et l’ajouter à la requête SQL |
| Ajouter une nouvelle page JSP | Controller + `WEB-INF/jsp` + navigation | Route GET, Model, JSP et lien |
| Modifier le rôle autorisé | `SecurityConfig` | Changer `hasRole` ou utiliser `hasAnyRole` |
| Modifier la page après connexion | `SecurityConfig` | Changer `defaultSuccessUrl` |
| Ajouter un rôle dans le formulaire | `UtilisateurService.listerRoles()` | Ajouter le libellé dans la liste |
| Changer le hash de mot de passe | `SecurityConfig` + `UtilisateurService` | Modifier l’encodeur avec prudence |
| Ajouter un champ livreur | Entité + JSP formulaire + JSP liste | Ajouter propriété, input et colonne |
| Ajouter un filtre livreur | Controller + service + JSP | Paramètre GET, prédicat et champ HTML |
| Ajouter un calcul SQL | `StatistiqueService` | Ajouter une agrégation paramétrée |
| Corriger un style | `assets/css/jsp.css` | Chercher la classe présente dans la JSP |
| Corriger un texte du menu | `fragments/navigation.jspf` | Modifier le libellé sans toucher aux pages |

## 9. Méthode sûre pour ajouter un nouvel indicateur

Exemple : afficher le nombre de lots humides.

1. Ajouter `nombreLotsHumides` dans `StatistiqueCollecte`.
2. Dans `calculerCollecte()`, ajouter :

```sql
COUNT(CASE WHEN taux_humidite > 15 THEN 1 END) AS nombre_lots_humides
```

3. Lire l’alias dans le constructeur de `StatistiqueCollecte`.
4. Afficher `${tableau.collecte.nombreLotsHumides}` dans la JSP.
5. Ajouter une assertion dans `StatistiqueServiceIntegrationTests`.
6. Lancer `mvn test`.

Ordre à retenir : DTO → service → contrôleur seulement si nécessaire → JSP → test.

## 10. Méthode sûre pour ajouter un filtre

1. Ajouter le champ HTML avec un attribut `name`.
2. Recevoir le même nom avec `@RequestParam` dans le contrôleur.
3. Passer la valeur au service.
4. Valider ou normaliser la valeur dans le service.
5. Ajouter un paramètre nommé SQL, jamais concaténer une valeur utilisateur dans la requête.
6. Remettre la valeur choisie dans `Model` afin que le formulaire la conserve.
7. Tester une valeur valide, vide et incorrecte.

## 11. Tests et rôle de chaque classe

| Classe de test | Ce qu’elle vérifie |
|---|---|
| `ApplicationTests` | Création du contexte Spring |
| `MvcIntegrationTests` | Session, sécurité, CSRF et CRUD PRG |
| `JspRenderingIntegrationTests` | Vrai Tomcat, Jasper, JSP, CSS et WebJar Chart.js |
| `StatistiqueServiceIntegrationTests` | Calculs, réduction, transformation et validation des dates |

Commandes :

```powershell
mvn test
mvn -Dtest=StatistiqueServiceIntegrationTests test
mvn -Dtest=JspRenderingIntegrationTests test
node --check src/main/resources/static/assets/js/statistiques-graphiques.js
```

Ne pas considérer uniquement la compilation Java comme une validation JSP : les JSP sont réellement compilées par Jasper dans `JspRenderingIntegrationTests`.

## 12. Problèmes fréquents

### La page statistique affiche zéro

Vérifier :

1. les dates du filtre ;
2. la présence de lignes dans les tables sources ;
3. que `detail_transaction` référence bien une fourniture ;
4. que `detail_lot_transforme` référence bien une transformation et un produit ;
5. que les dates PostgreSQL sont comprises entre les deux bornes.

### Le graphique Chart.js est vide

Vérifier :

1. qu’il existe des lignes dans le tableau des transactions ;
2. que `/webjars/chart.js/4.4.1/dist/chart.umd.js` répond HTTP 200 ;
3. que `statistiques-graphiques.js` ne contient pas d’erreur ;
4. la console du navigateur ;
5. un rechargement `Ctrl + F5`.

### Une page protégée redirige vers la connexion

La session a expiré ou l’utilisateur n’est pas authentifié. Vérifier `JSESSIONID`, le compte et le rôle.

### Un POST retourne 403

Le jeton CSRF manque probablement dans le formulaire.

### Une JSP retourne 500

Vérifier :

- le nom de la vue retourné par le contrôleur ;
- le fichier sous `WEB-INF/jsp` ;
- les balises JSTL ;
- le nom exact de l’attribut placé dans `Model` ;
- `mvn -Dtest=JspRenderingIntegrationTests test`.

### Le CSS n’est pas chargé

Les ressources publiques doivent être sous `src/main/resources/static/assets/` et accessibles par `/assets/**` dans `SecurityConfig`.

### Les accents deviennent `DÃ©connexion`

Vérifier :

- `<%@ page pageEncoding="UTF-8" %>` dans JSP/JSPF ;
- `<meta charset="UTF-8">` ;
- les propriétés `server.servlet.encoding.*` ;
- l’enregistrement UTF-8 du fichier ;
- le redémarrage de Tomcat pour forcer une nouvelle compilation JSP.

## 13. Annotations importantes à savoir expliquer

| Annotation | Explication courte |
|---|---|
| `@Entity` | La classe correspond à une table JPA |
| `@Table` | Fixe explicitement le nom SQL |
| `@Id` | Clé primaire |
| `@GeneratedValue` | Identifiant généré par PostgreSQL |
| `@ManyToOne` | Plusieurs lignes peuvent référencer la même entité |
| `@JoinColumn` | Nom de la clé étrangère |
| `@Controller` | Retourne des vues MVC |
| `@RestController` | Retourne directement du JSON |
| `@GetMapping` | Route HTTP de lecture |
| `@PostMapping` | Route HTTP d’écriture/formulaire |
| `@RequestParam` | Paramètre de requête ou formulaire |
| `@PathVariable` | Valeur intégrée dans l’URL |
| `@Service` | Couche de logique métier |
| `@Transactional` | Tout réussit ou tout est annulé |
| `@ConditionalOnProperty` | Active un composant selon une propriété |
| `@SpringBootTest` | Charge le contexte réel pour un test |

## 14. Questions orales probables

### Pourquoi les JSP sont-elles dans `WEB-INF` ?

Pour empêcher leur accès direct. Toute vue doit passer par un contrôleur, donc par le `Model` et la sécurité.

### Pourquoi ne pas créer une table statistique ?

Une statistique est calculée depuis les données sources. Une table supplémentaire dupliquerait les valeurs et pourrait devenir incohérente.

### Pourquoi utiliser JDBC dans `StatistiqueService` ?

Les agrégations `SUM`, `AVG`, `COUNT`, regroupements et jointures sont plus claires en SQL. JPA reste utilisé pour définir le schéma et les relations.

### Pourquoi `BigDecimal` pour les statistiques ?

Pour éviter les imprécisions binaires de `double` dans les montants, taux et quantités décimales.

### Pourquoi le contrôleur ne calcule-t-il rien ?

Pour respecter la séparation des responsabilités et rendre les règles métier testables sans serveur HTTP.

### Pourquoi JavaScript est-il encore présent ?

Uniquement pour des aides d’interface : graphique, export et impression. Les données et le HTML principal restent côté serveur.

### Pourquoi les données de test sont-elles transactionnelles ?

Pour vérifier de vraies requêtes PostgreSQL sans laisser de lignes artificielles après les tests.

### Pourquoi les identifiants utilisent-ils `Integer` ?

Parce que le schéma demandé utilise `int`, et toutes les couches doivent employer le même type.

## 15. Protocole de modification pendant la soutenance

1. Reformuler la demande en une phrase.
2. Identifier la couche concernée avec le tableau de la section 8.
3. Modifier le plus petit nombre de fichiers possible.
4. Ne pas déplacer une règle métier dans la JSP ou le contrôleur.
5. Compiler ou exécuter le test ciblé.
6. Redémarrer l’application si Java, JSP, sécurité ou configuration a changé.
7. Utiliser `Ctrl + F5` si CSS ou JavaScript a changé.
8. Expliquer oralement le trajet : entrée → calcul → Model → JSP.

## 16. Commandes essentielles

```powershell
# Démarrer
mvn spring-boot:run

# Tous les tests
mvn test

# Générer le WAR
mvn package

# Lancer le WAR
java -jar target/demo-0.0.1-SNAPSHOT.war

# Vérifier JavaScript
node --check src/main/resources/static/assets/js/statistiques-graphiques.js

# Vérifier les problèmes de diff
git diff --check
```

Adresse principale du module :

```text
http://localhost:8080/admin/statistiques
```

## 17. Ordre conseillé pour étudier le projet

1. `Application.java` et `application.properties`.
2. `SecurityConfig`, `CustomUserDetailsService`, `Utilisateur`.
3. Une chaîne CRUD simple : `LivreurMvcController` → `LivreurService` → `LivreurRepository` → JSP.
4. `StatistiqueMvcController` et son filtre de dates.
5. `StatistiqueService`, méthode par méthode.
6. Les records de `dto/statistique`.
7. `statistiques/index.jsp`.
8. `statistiques-graphiques.js`.
9. `StatistiqueServiceIntegrationTests`.
10. `JspRenderingIntegrationTests`.

À chaque étape, répondre à trois questions : quelle donnée entre, quelle opération est réalisée, et quel résultat sort ?
