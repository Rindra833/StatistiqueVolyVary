package com.volyVary.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.volyVary.dto.statistique.LigneTransactionStatistique;
import com.volyVary.dto.statistique.LigneTransformationStatistique;
import com.volyVary.dto.statistique.PeriodeStatistique;
import com.volyVary.dto.statistique.PointMensuelStatistique;
import com.volyVary.dto.statistique.ResumeGlobalStatistique;
import com.volyVary.dto.statistique.StatistiqueCollecte;
import com.volyVary.dto.statistique.StatistiqueTransformation;
import com.volyVary.dto.statistique.TableauStatistique;

@Service
public class StatistiqueService {

    private static final BigDecimal CENT = BigDecimal.valueOf(100);
    private static final BigDecimal TAUX_REDUCTION_HUMIDITE = new BigDecimal("0.20");

    private final NamedParameterJdbcTemplate baseDeDonnees;

    /**
     * Reçoit l'outil JDBC nommé de Spring. Le service utilise des requêtes d'agrégation en lecture
     * seule, car créer des entités statistiques séparées dupliquerait inutilement les données des
     * modules transaction, collecte et transformation.
     */
    public StatistiqueService(NamedParameterJdbcTemplate baseDeDonnees) {
        this.baseDeDonnees = baseDeDonnees;
    }

    /**
     * Construit toutes les informations nécessaires à la page statistique. La période est d'abord
     * normalisée, puis chaque famille d'indicateurs est calculée indépendamment. Cette séparation
     * permet de modifier un calcul métier sans perturber les trois autres blocs de la page.
     */
    public TableauStatistique calculerTableau(LocalDate dateDebut, LocalDate dateFin) {
        PeriodeStatistique periode = determinerPeriode(dateDebut, dateFin);
        List<LigneTransactionStatistique> transactions = calculerTransactions(periode);
        StatistiqueCollecte collecte = calculerCollecte(periode);
        StatistiqueTransformation transformation = calculerTransformation(periode);

        long nombreTransactions = transactions.stream()
            .mapToLong(LigneTransactionStatistique::nombre)
            .sum();
        BigDecimal chiffreAffaires = transactions.stream()
            .map(LigneTransactionStatistique::montant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        ResumeGlobalStatistique global = new ResumeGlobalStatistique(
            nombreTransactions,
            chiffreAffaires,
            collecte.quantiteTotale(),
            transformation.paddyTransforme()
        );

        return new TableauStatistique(
            periode,
            global,
            transactions,
            collecte,
            transformation,
            calculerEvolution(periode)
        );
    }

    /**
     * Valide les deux dates reçues depuis le formulaire. Lorsque les deux champs sont vides, le mois
     * actuel devient la période par défaut. Une seule date ou un ordre inversé produit une erreur
     * explicite que le contrôleur pourra afficher sans lancer de requête statistique incorrecte.
     */
    private PeriodeStatistique determinerPeriode(LocalDate dateDebut, LocalDate dateFin) {
        LocalDate aujourdHui = LocalDate.now();
        if (dateDebut == null && dateFin == null) {
            return new PeriodeStatistique(
                "mois",
                "Mois actuel",
                aujourdHui.withDayOfMonth(1),
                aujourdHui
            );
        }
        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Les deux dates doivent être renseignées.");
        }
        if (dateDebut.isAfter(dateFin)) {
            throw new IllegalArgumentException(
                "La date de début doit être antérieure ou égale à la date de fin."
            );
        }
        return new PeriodeStatistique(
            "personnalisee",
            "Période personnalisée",
            dateDebut,
            dateFin
        );
    }

    /**
     * Agrège les transactions par type en utilisant les prix des fournitures et les quantités de
     * détail. Le LEFT JOIN conserve dans le résultat un type existant même s'il ne possède aucune
     * transaction pendant la période sélectionnée.
     */
    private List<LigneTransactionStatistique> calculerTransactions(PeriodeStatistique periode) {
        String requete = """
            SELECT tt.libelle AS type,
                   COUNT(DISTINCT t.id) AS nombre,
                   COALESCE(SUM(dt.quantite), 0) AS quantite,
                   COALESCE(SUM(dt.quantite * f.prix), 0) AS montant
            FROM type_transaction tt
            LEFT JOIN "transaction" t
                   ON t.id_type = tt.id
                  AND t.date BETWEEN :debut AND :fin
            LEFT JOIN detail_transaction dt ON dt.id_transaction = t.id
            LEFT JOIN fourniture f ON f.id = dt.id_fourniture
            GROUP BY tt.id, tt.libelle
            ORDER BY tt.id
            """;

        return baseDeDonnees.queryForList(requete, parametres(periode)).stream()
            .map(ligne -> new LigneTransactionStatistique(
                String.valueOf(ligne.get("type")),
                convertirLong(ligne.get("nombre")),
                convertirLong(ligne.get("quantite")),
                convertirDecimal(ligne.get("montant"))
            ))
            .toList();
    }

    /**
     * Calcule les volumes et coûts de collecte ainsi que l'humidité moyenne. La réduction estimée
     * applique la règle du cahier des charges : vingt pour cent du prix lorsque l'humidité dépasse
     * strictement quinze pour cent.
     */
    private StatistiqueCollecte calculerCollecte(PeriodeStatistique periode) {
        String requete = """
            SELECT COUNT(*) AS nombre_lots,
                   COALESCE(SUM(quantite), 0) AS quantite_totale,
                   COALESCE(SUM(prix_collecte), 0) AS cout_total,
                   COALESCE(AVG(taux_humidite), 0) AS humidite_moyenne,
                   COALESCE(SUM(
                       CASE WHEN taux_humidite > 15
                            THEN prix_collecte * :tauxReduction
                            ELSE 0 END
                   ), 0) AS reduction_estimee
            FROM lot_paddy
            WHERE date BETWEEN :debut AND :fin
            """;

        MapSqlParameterSource parametres = parametres(periode)
            .addValue("tauxReduction", TAUX_REDUCTION_HUMIDITE);
        Map<String, Object> ligne = baseDeDonnees.queryForMap(requete, parametres);

        return new StatistiqueCollecte(
            convertirLong(ligne.get("nombre_lots")),
            convertirDecimal(ligne.get("quantite_totale")),
            convertirDecimal(ligne.get("cout_total")),
            convertirDecimal(ligne.get("humidite_moyenne")),
            convertirDecimal(ligne.get("reduction_estimee"))
        );
    }

    /**
     * Calcule le paddy consommé, le coût des opérations et la répartition des produits obtenus.
     * Les pourcentages sont calculés sur la production réellement enregistrée dans les détails et
     * permettent de comparer le résultat aux proportions 65/20/10/5 du cahier des charges.
     */
    private StatistiqueTransformation calculerTransformation(PeriodeStatistique periode) {
        String requeteEntete = """
            SELECT COUNT(*) AS nombre_lots,
                   COALESCE(SUM(quantite), 0) AS paddy_transforme,
                   COALESCE(SUM(prix_transformation), 0) AS cout_transformation
            FROM lot_paddy_transforme
            WHERE date BETWEEN :debut AND :fin
            """;
        Map<String, Object> entete = baseDeDonnees.queryForMap(
            requeteEntete,
            parametres(periode)
        );

        String requeteProduits = """
            SELECT p.nom AS produit,
                   COALESCE(SUM(
                       CASE WHEN lpt.id IS NOT NULL THEN dlt.quantite ELSE 0 END
                   ), 0) AS quantite
            FROM produit p
            LEFT JOIN detail_lot_transforme dlt ON dlt.id_produit = p.id
            LEFT JOIN lot_paddy_transforme lpt
                   ON lpt.id = dlt.id_lot_transforme
                  AND lpt.date BETWEEN :debut AND :fin
            GROUP BY p.id, p.nom
            ORDER BY p.id
            """;
        List<Map<String, Object>> lignes = baseDeDonnees.queryForList(
            requeteProduits,
            parametres(periode)
        );
        BigDecimal totalProduits = lignes.stream()
            .map(ligne -> convertirDecimal(ligne.get("quantite")))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<LigneTransformationStatistique> repartition = lignes.stream()
            .map(ligne -> {
                BigDecimal quantite = convertirDecimal(ligne.get("quantite"));
                BigDecimal pourcentage = totalProduits.signum() == 0
                    ? BigDecimal.ZERO
                    : quantite.multiply(CENT)
                        .divide(totalProduits, 1, RoundingMode.HALF_UP);
                return new LigneTransformationStatistique(
                    String.valueOf(ligne.get("produit")),
                    quantite,
                    pourcentage
                );
            })
            .toList();

        return new StatistiqueTransformation(
            convertirLong(entete.get("nombre_lots")),
            convertirDecimal(entete.get("paddy_transforme")),
            convertirDecimal(entete.get("cout_transformation")),
            totalProduits,
            repartition
        );
    }

    /**
     * Produit une série mensuelle limitée à douze mois. Les trois requêtes restent séparées pour
     * éviter un produit cartésien entre modules, puis leurs résultats sont réunis en Java avant de
     * calculer les largeurs des barres HTML affichées dans la JSP.
     */
    private List<PointMensuelStatistique> calculerEvolution(PeriodeStatistique periode) {
        YearMonth dernierMois = YearMonth.from(periode.fin());
        YearMonth premierMois = YearMonth.from(periode.debut());
        if (premierMois.isBefore(dernierMois.minusMonths(11))) {
            premierMois = dernierMois.minusMonths(11);
        }

        LocalDate debutEvolution = premierMois.atDay(1);
        MapSqlParameterSource parametres = new MapSqlParameterSource()
            .addValue("debut", debutEvolution)
            .addValue("fin", periode.fin());

        Map<YearMonth, Long> transactions = lireEvolutionTransactions(parametres);
        Map<YearMonth, BigDecimal> collectes = lireEvolutionDecimal(
            "lot_paddy",
            "quantite",
            parametres
        );
        Map<YearMonth, BigDecimal> transformations = lireEvolutionDecimal(
            "lot_paddy_transforme",
            "quantite",
            parametres
        );

        List<YearMonth> mois = new ArrayList<>();
        for (YearMonth courant = premierMois; !courant.isAfter(dernierMois);
             courant = courant.plusMonths(1)) {
            mois.add(courant);
        }

        long maximumTransactions = mois.stream()
            .mapToLong(moisCourant -> transactions.getOrDefault(moisCourant, 0L))
            .max()
            .orElse(0L);
        BigDecimal maximumCollecte = maximumDecimal(mois, collectes);
        BigDecimal maximumTransformation = maximumDecimal(mois, transformations);
        DateTimeFormatter formatMois = DateTimeFormatter.ofPattern("MMM yyyy", Locale.FRENCH);

        return mois.stream()
            .map(moisCourant -> {
                long nombreTransactions = transactions.getOrDefault(moisCourant, 0L);
                BigDecimal quantiteCollectee = collectes.getOrDefault(moisCourant, BigDecimal.ZERO);
                BigDecimal quantiteTransformee = transformations.getOrDefault(
                    moisCourant,
                    BigDecimal.ZERO
                );
                return new PointMensuelStatistique(
                    formatMois.format(moisCourant).replace(".", ""),
                    nombreTransactions,
                    quantiteCollectee,
                    quantiteTransformee,
                    calculerLargeur(BigDecimal.valueOf(nombreTransactions), BigDecimal.valueOf(maximumTransactions)),
                    calculerLargeur(quantiteCollectee, maximumCollecte),
                    calculerLargeur(quantiteTransformee, maximumTransformation)
                );
            })
            .toList();
    }

    /**
     * Lit le nombre mensuel de transactions. Les composantes année et mois sont utilisées au lieu
     * d'une fonction spécifique à PostgreSQL afin de garder la requête compréhensible et portable.
     */
    private Map<YearMonth, Long> lireEvolutionTransactions(MapSqlParameterSource parametres) {
        String requete = """
            SELECT EXTRACT(YEAR FROM date) AS annee,
                   EXTRACT(MONTH FROM date) AS mois,
                   COUNT(*) AS valeur
            FROM "transaction"
            WHERE date BETWEEN :debut AND :fin
            GROUP BY EXTRACT(YEAR FROM date), EXTRACT(MONTH FROM date)
            """;
        Map<YearMonth, Long> resultat = new HashMap<>();
        baseDeDonnees.queryForList(requete, parametres).forEach(ligne -> resultat.put(
            creerMois(ligne),
            convertirLong(ligne.get("valeur"))
        ));
        return resultat;
    }

    /**
     * Lit une somme mensuelle dans l'une des deux tables de volume autorisées. La liste blanche
     * empêche qu'un nom de table ou de colonne provenant d'une requête HTTP soit injecté en SQL.
     */
    private Map<YearMonth, BigDecimal> lireEvolutionDecimal(
        String table,
        String colonne,
        MapSqlParameterSource parametres
    ) {
        Map<String, String> sourcesAutorisees = new LinkedHashMap<>();
        sourcesAutorisees.put("lot_paddy", "quantite");
        sourcesAutorisees.put("lot_paddy_transforme", "quantite");
        if (!colonne.equals(sourcesAutorisees.get(table))) {
            throw new IllegalArgumentException("Source statistique mensuelle non autorisée");
        }

        String requete = "SELECT EXTRACT(YEAR FROM date) AS annee, "
            + "EXTRACT(MONTH FROM date) AS mois, COALESCE(SUM(" + colonne + "), 0) AS valeur "
            + "FROM " + table + " WHERE date BETWEEN :debut AND :fin "
            + "GROUP BY EXTRACT(YEAR FROM date), EXTRACT(MONTH FROM date)";
        Map<YearMonth, BigDecimal> resultat = new HashMap<>();
        baseDeDonnees.queryForList(requete, parametres).forEach(ligne -> resultat.put(
            creerMois(ligne),
            convertirDecimal(ligne.get("valeur"))
        ));
        return resultat;
    }

    /**
     * Convertit les colonnes numériques année et mois retournées par JDBC en une clé YearMonth.
     */
    private YearMonth creerMois(Map<String, Object> ligne) {
        int annee = (int) convertirLong(ligne.get("annee"));
        int mois = (int) convertirLong(ligne.get("mois"));
        return YearMonth.of(annee, mois);
    }

    /**
     * Recherche le maximum d'une série décimale pour normaliser les barres du graphique HTML.
     */
    private BigDecimal maximumDecimal(
        List<YearMonth> mois,
        Map<YearMonth, BigDecimal> valeurs
    ) {
        return mois.stream()
            .map(moisCourant -> valeurs.getOrDefault(moisCourant, BigDecimal.ZERO))
            .max(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);
    }

    /**
     * Transforme une valeur en pourcentage entier compris entre zéro et cent pour l'attribut CSS
     * width. Une série entièrement vide retourne zéro et ne provoque aucune division par zéro.
     */
    private int calculerLargeur(BigDecimal valeur, BigDecimal maximum) {
        if (maximum.signum() == 0) {
            return 0;
        }
        return valeur.multiply(CENT)
            .divide(maximum, 0, RoundingMode.HALF_UP)
            .intValue();
    }

    /**
     * Crée les paramètres communs de début et de fin utilisés par toutes les agrégations SQL.
     */
    private MapSqlParameterSource parametres(PeriodeStatistique periode) {
        return new MapSqlParameterSource()
            .addValue("debut", periode.debut())
            .addValue("fin", periode.fin());
    }

    /**
     * Convertit de façon uniforme les types numériques renvoyés par PostgreSQL en BigDecimal.
     */
    private BigDecimal convertirDecimal(Object valeur) {
        if (valeur == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(valeur.toString()).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Convertit de façon uniforme les résultats COUNT, SUM et EXTRACT en entier long.
     */
    private long convertirLong(Object valeur) {
        if (valeur == null) {
            return 0L;
        }
        return new BigDecimal(valeur.toString()).longValue();
    }
}
