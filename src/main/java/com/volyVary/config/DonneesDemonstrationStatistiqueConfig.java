package com.volyVary.config;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ajoute un scénario de démonstration uniquement lorsque la propriété dédiée est activée.
 * Toutes les références créées commencent par DEMO-STAT- pour rester identifiables.
 */
@Component
@ConditionalOnProperty(
    name = "app.statistiques.donnees-demonstration",
    havingValue = "true"
)
public class DonneesDemonstrationStatistiqueConfig implements CommandLineRunner {

    private static final String PREFIXE = "DEMO-STAT-";

    private final JdbcTemplate baseDeDonnees;

    /**
     * Reçoit l'accès JDBC utilisé pour insérer le scénario sans ajouter des repositories qui ne
     * seraient nécessaires qu'à la démonstration.
     */
    public DonneesDemonstrationStatistiqueConfig(JdbcTemplate baseDeDonnees) {
        this.baseDeDonnees = baseDeDonnees;
    }

    /**
     * Vérifie si le scénario existe déjà puis crée six mois cohérents de transactions, collectes
     * et transformations. La transaction Spring garantit qu'un arrêt pendant l'insertion ne laisse
     * pas un jeu de données incomplet dans PostgreSQL.
     */
    @Override
    @Transactional
    public void run(String... arguments) {
        Integer nombreExistant = baseDeDonnees.queryForObject(
            "SELECT COUNT(*) FROM \"transaction\" WHERE reference LIKE ?",
            Integer.class,
            PREFIXE + "%"
        );
        if (nombreExistant != null && nombreExistant > 0) {
            return;
        }

        Integer venteDirecte = obtenirOuCreerType("Vente directe");
        Integer venteCredit = obtenirOuCreerType("Vente à crédit");
        Integer location = obtenirOuCreerType("Location");
        Integer[] types = {venteDirecte, venteCredit, location};

        Integer fourniture = creerFournitureDemonstration();
        Integer rizBlanc = obtenirOuCreerProduit("Riz blanc", "2600.00");
        Integer akofomBary = obtenirOuCreerProduit("Akofom-bary", "600.00");
        Integer tofomBary = obtenirOuCreerProduit("Tofom-bary", "400.00");
        Integer varyMadinika = obtenirOuCreerProduit("Vary madinika", "1200.00");

        for (int index = 5; index >= 0; index--) {
            YearMonth mois = YearMonth.now().minusMonths(index);
            LocalDate date = mois.atDay(Math.min(10, mois.lengthOfMonth()));
            int progression = 6 - index;
            insererMois(
                mois,
                date,
                progression,
                types[progression % types.length],
                fourniture,
                rizBlanc,
                akofomBary,
                tofomBary,
                varyMadinika
            );
        }
    }

    /**
     * Insère les lignes reliées d'un mois. Les quantités progressent volontairement pour produire
     * des barres visibles et différentes sur le graphique de la page statistique.
     */
    private void insererMois(
        YearMonth mois,
        LocalDate date,
        int progression,
        Integer idType,
        Integer idFourniture,
        Integer rizBlanc,
        Integer akofomBary,
        Integer tofomBary,
        Integer varyMadinika
    ) {
        String suffixe = mois.toString();
        Integer idTransaction = insererEtRetournerId(
            "INSERT INTO \"transaction\" (id_type, reference, date) VALUES (?, ?, ?) RETURNING id",
            idType,
            PREFIXE + "TR-" + suffixe,
            date
        );
        baseDeDonnees.update(
            "INSERT INTO detail_transaction (id_transaction, id_fourniture, quantite) VALUES (?, ?, ?)",
            idTransaction,
            idFourniture,
            progression * 2
        );

        BigDecimal quantiteCollectee = BigDecimal.valueOf(400L + progression * 100L);
        BigDecimal humidite = progression % 2 == 0
            ? new BigDecimal("16.50")
            : new BigDecimal("13.50");
        Integer idLot = insererEtRetournerId(
            "INSERT INTO lot_paddy "
                + "(reference, quantite, taux_humidite, date, prix_collecte) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING id",
            PREFIXE + "LOT-" + suffixe,
            quantiteCollectee,
            humidite,
            date,
            quantiteCollectee.multiply(new BigDecimal("900.00"))
        );

        BigDecimal quantiteTransformee = quantiteCollectee
            .multiply(new BigDecimal("0.80"))
            .setScale(2, RoundingMode.HALF_UP);
        Integer idTransformation = insererEtRetournerId(
            "INSERT INTO lot_paddy_transforme "
                + "(reference, quantite, date, prix_transformation) VALUES (?, ?, ?, ?) RETURNING id",
            PREFIXE + "TF-" + suffixe,
            quantiteTransformee,
            date,
            quantiteTransformee.multiply(new BigDecimal("150.00"))
        );

        insererProduitTransforme(idTransformation, idLot, rizBlanc, quantiteTransformee, "0.65", date);
        insererProduitTransforme(idTransformation, idLot, akofomBary, quantiteTransformee, "0.20", date);
        insererProduitTransforme(idTransformation, idLot, tofomBary, quantiteTransformee, "0.10", date);
        insererProduitTransforme(idTransformation, idLot, varyMadinika, quantiteTransformee, "0.05", date);
    }

    /**
     * Ajoute une ligne de produit transformé en appliquant l'un des taux 65/20/10/5 imposés par le
     * cahier des charges.
     */
    private void insererProduitTransforme(
        Integer idTransformation,
        Integer idLot,
        Integer idProduit,
        BigDecimal quantiteTransformee,
        String taux,
        LocalDate date
    ) {
        BigDecimal quantiteProduit = quantiteTransformee
            .multiply(new BigDecimal(taux))
            .setScale(2, RoundingMode.HALF_UP);
        baseDeDonnees.update(
            "INSERT INTO detail_lot_transforme "
                + "(id_lot_transforme, id_lot, id_produit, quantite, date) VALUES (?, ?, ?, ?, ?)",
            idTransformation,
            idLot,
            idProduit,
            quantiteProduit,
            date
        );
    }

    /**
     * Réutilise un type créé par le module transaction ou l'ajoute s'il n'existe pas encore. Cette
     * stratégie évite les doublons lors de la fusion avec le travail de l'autre équipe.
     */
    private Integer obtenirOuCreerType(String libelle) {
        return obtenirIdentifiantExistant(
            "SELECT id FROM type_transaction WHERE LOWER(libelle) = LOWER(?) ORDER BY id LIMIT 1",
            libelle
        );
    }

    /**
     * Réutilise un produit du catalogue ou le crée avec un prix de démonstration. Les noms restent
     * ceux du cahier des charges et pourront donc être partagés par le module transformation.
     */
    private Integer obtenirOuCreerProduit(String nom, String prix) {
        Integer identifiant = rechercherIdentifiant(
            "SELECT id FROM produit WHERE LOWER(nom) = LOWER(?) ORDER BY id LIMIT 1",
            nom
        );
        if (identifiant != null) {
            return identifiant;
        }
        return insererEtRetournerId(
            "INSERT INTO produit (nom, prix_unitaire) VALUES (?, ?) RETURNING id",
            nom,
            new BigDecimal(prix)
        );
    }

    /**
     * Crée une fourniture dédiée dont le nom préfixé permet un nettoyage sûr sans toucher aux
     * fournitures métier déjà enregistrées.
     */
    private Integer creerFournitureDemonstration() {
        return insererEtRetournerId(
            "INSERT INTO fourniture "
                + "(nom, categorie, quantite, prix, date, fournisseur) "
                + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id",
            PREFIXE + "SEMENCES",
            "Semences",
            500,
            25000.0,
            LocalDate.now(),
            "Démonstration"
        );
    }

    /**
     * Recherche un type puis le crée seulement lorsqu'aucune ligne équivalente n'est disponible.
     */
    private Integer obtenirIdentifiantExistant(String requete, String valeur) {
        Integer identifiant = rechercherIdentifiant(requete, valeur);
        if (identifiant != null) {
            return identifiant;
        }
        return insererEtRetournerId(
            "INSERT INTO type_transaction (libelle) VALUES (?) RETURNING id",
            valeur
        );
    }

    /**
     * Retourne le premier identifiant trouvé ou null sans utiliser une exception comme résultat
     * normal lorsqu'un catalogue est encore vide.
     */
    private Integer rechercherIdentifiant(String requete, String valeur) {
        return baseDeDonnees.query(
            requete,
            resultat -> resultat.next() ? resultat.getInt("id") : null,
            valeur
        );
    }

    /**
     * Exécute un INSERT PostgreSQL et récupère immédiatement l'identifiant généré pour conserver
     * les relations entre les différentes lignes du scénario.
     */
    private Integer insererEtRetournerId(String requete, Object... parametres) {
        return baseDeDonnees.queryForObject(requete, Integer.class, parametres);
    }
}
