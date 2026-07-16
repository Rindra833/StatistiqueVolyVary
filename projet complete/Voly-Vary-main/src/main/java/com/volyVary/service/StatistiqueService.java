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

import com.volyVary.dto.*;

@Service
public class StatistiqueService {

    private static final BigDecimal CENT = BigDecimal.valueOf(100);

    private final NamedParameterJdbcTemplate baseDeDonnees;

    /**
     * Reçoit l'outil JDBC de Spring. Le module statistique lit directement les tables des autres
     * modules : il ne crée donc aucune copie des modèles Client, Employee, Transaction ou Paddy.
     */
    public StatistiqueService(NamedParameterJdbcTemplate baseDeDonnees) {
        this.baseDeDonnees = baseDeDonnees;
    }

    /**
     * Construit les quatre blocs du dashboard pour une même période. Chaque calcul reste isolé afin
     * qu'une évolution du module transaction, collecte ou transformation soit facile à localiser.
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
     * Utilise le mois actuel lorsque les deux champs sont vides et refuse une période incomplète ou
     * inversée. La borne affichée à l'utilisateur reste inclusive.
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
     * Agrège les transactions par type. Le chiffre d'affaires vient de transaction.montant_total,
     * qui est aussi la source utilisée par la carte « Recette totale » du module Transaction. Les
     * quantités sont d'abord regroupées par transaction pour éviter qu'un total soit multiplié
     * lorsqu'une transaction contient plusieurs fournitures.
     */
    private List<LigneTransactionStatistique> calculerTransactions(PeriodeStatistique periode) {
        String requete = """
            SELECT tt.libelle AS type,
                   COUNT(t.id) AS nombre,
                   COALESCE(SUM(details.quantite), 0) AS quantite,
                   COALESCE(SUM(t.montant_total), 0) AS montant
            FROM type_transaction tt
            LEFT JOIN "transaction" t
                   ON t.id_type = tt.id
                  AND t.date >= :debut
                  AND t.date < :finExclusive
            LEFT JOIN (
                SELECT id_transaction, SUM(quantite) AS quantite
                FROM detail_transaction
                GROUP BY id_transaction
            ) details ON details.id_transaction = t.id
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
     * Calcule les indicateurs de collecte à partir des colonnes du projet complet. La réduction est
     * la différence entre le prix théorique du lot et son prix_collecte réellement enregistré ; elle
     * respecte ainsi automatiquement les paliers du module collecte, sans règle dupliquée ici.
     */
    private StatistiqueCollecte calculerCollecte(PeriodeStatistique periode) {
        String requete = """
            SELECT COUNT(*) AS nombre_lots,
                   COALESCE(SUM(lp.quantite), 0) AS quantite_totale,
                   COALESCE(SUM(lp.prix_collecte), 0) AS cout_total,
                   COALESCE(AVG(lp.taux_humidite), 0) AS humidite_moyenne,
                   COALESCE(SUM(
                       GREATEST(
                           COALESCE(lp.quantite, 0) * COALESCE(c.prix_unitaire, 0)
                           - COALESCE(lp.prix_collecte, 0),
                           0
                       )
                   ), 0) AS reduction_estimee
            FROM lot_paddy lp
            LEFT JOIN collecte c ON c.id = lp.id_collecte
            WHERE lp.date >= :debut
              AND lp.date < :finExclusive
            """;

        Map<String, Object> ligne = baseDeDonnees.queryForMap(requete, parametres(periode));

        return new StatistiqueCollecte(
            convertirLong(ligne.get("nombre_lots")),
            convertirDecimal(ligne.get("quantite_totale")),
            convertirDecimal(ligne.get("cout_total")),
            convertirDecimal(ligne.get("humidite_moyenne")),
            convertirDecimal(ligne.get("reduction_estimee"))
        );
    }


    /**
     * Lit les lots transformés et leurs détails déjà produits par le module transformation. Aucun
     * modèle statistique parallèle n'est nécessaire : les noms de tables et colonnes restent ceux du
     * projet complet.
     */
    private StatistiqueTransformation calculerTransformation(PeriodeStatistique periode) {
        String requeteEntete = """
            SELECT COUNT(*) AS nombre_lots,
                   COALESCE(SUM(quantite), 0) AS paddy_transforme,
                   COALESCE(SUM(prix_transformation), 0) AS cout_transformation
            FROM lot_paddy_transforme
            WHERE date >= :debut
              AND date < :finExclusive
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
                  AND lpt.date >= :debut
                  AND lpt.date < :finExclusive
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
     * Produit au maximum douze points mensuels. Les trois séries sont requêtées séparément pour ne
     * pas créer de produit cartésien entre les tables des modules.
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
            .addValue("finExclusive", periode.fin().plusDays(1));

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
     * Compte les transactions de chaque mois dans la période demandée.
     */
    private Map<YearMonth, Long> lireEvolutionTransactions(MapSqlParameterSource parametres) {
        String requete = """
            SELECT EXTRACT(YEAR FROM date) AS annee,
                   EXTRACT(MONTH FROM date) AS mois,
                   COUNT(*) AS valeur
            FROM "transaction"
            WHERE date >= :debut
              AND date < :finExclusive
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
     * Additionne une colonne mensuelle parmi une liste blanche interne. Aucun nom de table reçu du
     * navigateur n'est injecté dans cette requête.
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
            + "FROM " + table + " WHERE date >= :debut AND date < :finExclusive "
            + "GROUP BY EXTRACT(YEAR FROM date), EXTRACT(MONTH FROM date)";
        Map<YearMonth, BigDecimal> resultat = new HashMap<>();
        baseDeDonnees.queryForList(requete, parametres).forEach(ligne -> resultat.put(
            creerMois(ligne),
            convertirDecimal(ligne.get("valeur"))
        ));
        return resultat;
    }

    private YearMonth creerMois(Map<String, Object> ligne) {
        int annee = (int) convertirLong(ligne.get("annee"));
        int mois = (int) convertirLong(ligne.get("mois"));
        return YearMonth.of(annee, mois);
    }

    private BigDecimal maximumDecimal(
        List<YearMonth> mois,
        Map<YearMonth, BigDecimal> valeurs
    ) {
        return mois.stream()
            .map(moisCourant -> valeurs.getOrDefault(moisCourant, BigDecimal.ZERO))
            .max(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);
    }

    private int calculerLargeur(BigDecimal valeur, BigDecimal maximum) {
        if (maximum.signum() == 0) {
            return 0;
        }
        return valeur.multiply(CENT)
            .divide(maximum, 0, RoundingMode.HALF_UP)
            .intValue();
    }

    /**
     * Convertit la date de fin inclusive du formulaire en borne SQL exclusive au lendemain. Cette
     * méthode inclut toutes les opérations du dernier jour, même celles enregistrées avec une heure.
     */
    private MapSqlParameterSource parametres(PeriodeStatistique periode) {
        return new MapSqlParameterSource()
            .addValue("debut", periode.debut())
            .addValue("finExclusive", periode.fin().plusDays(1));
    }

    private BigDecimal convertirDecimal(Object valeur) {
        if (valeur == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(valeur.toString()).setScale(2, RoundingMode.HALF_UP);
    }

    private long convertirLong(Object valeur) {
        if (valeur == null) {
            return 0L;
        }
        return new BigDecimal(valeur.toString()).longValue();
    }
}
