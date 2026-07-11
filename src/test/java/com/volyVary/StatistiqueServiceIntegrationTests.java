package com.volyVary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.volyVary.dto.statistique.LigneTransactionStatistique;
import com.volyVary.dto.statistique.LigneTransformationStatistique;
import com.volyVary.dto.statistique.TableauStatistique;
import com.volyVary.model.Fourniture;
import com.volyVary.repository.FournitureRepository;
import com.volyVary.service.StatistiqueService;

@SpringBootTest(properties = "app.statistiques.donnees-demonstration=false")
@Transactional
class StatistiqueServiceIntegrationTests {

    @Autowired
    private StatistiqueService statistiqueService;

    @Autowired
    private FournitureRepository fournitureRepository;

    @Autowired
    private JdbcTemplate baseDeDonnees;

    /**
     * Insère un petit scénario complet dans les sept tables sources, vérifie les cinq groupes
     * d'indicateurs demandés, puis laisse Spring annuler automatiquement la transaction du test.
     * Les données artificielles ne restent donc jamais dans la base PostgreSQL partagée.
     */
    @Test
    void calculeLesStatistiquesAvecDesDonneesReelles() {
        LocalDate debutAnnee = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        TableauStatistique avantInsertion = statistiqueService.calculerTableau(
            debutAnnee,
            LocalDate.now()
        );
        String suffixe = UUID.randomUUID().toString().substring(0, 8);
        LocalDate date = LocalDate.now();

        Fourniture fourniture = creerFourniture(suffixe, date);
        Integer idType = insererEtRetournerId(
            "INSERT INTO type_transaction (libelle) VALUES (?) RETURNING id",
            "Vente test " + suffixe
        );
        Integer idTransaction = insererEtRetournerId(
            "INSERT INTO \"transaction\" (id_type, reference, date) VALUES (?, ?, ?) RETURNING id",
            idType,
            "TR-TEST-" + suffixe,
            date
        );
        baseDeDonnees.update(
            "INSERT INTO detail_transaction (id_transaction, id_fourniture, quantite) VALUES (?, ?, ?)",
            idTransaction,
            fourniture.getId(),
            3
        );

        Integer premierLot = insererLot("LOT-A-" + suffixe, "100.00", "14.00", "100000.00", date);
        insererLot("LOT-B-" + suffixe, "200.00", "16.00", "200000.00", date);
        Integer idTransformation = insererEtRetournerId(
            "INSERT INTO lot_paddy_transforme "
                + "(reference, quantite, date, prix_transformation) VALUES (?, ?, ?, ?) RETURNING id",
            "TF-TEST-" + suffixe,
            new BigDecimal("300.00"),
            date,
            new BigDecimal("50000.00")
        );

        insererProduitTransforme("Riz blanc test " + suffixe, "195.00", idTransformation, premierLot, date);
        insererProduitTransforme("Akofom-bary test " + suffixe, "60.00", idTransformation, premierLot, date);
        insererProduitTransforme("Tofom-bary test " + suffixe, "30.00", idTransformation, premierLot, date);
        insererProduitTransforme("Vary madinika test " + suffixe, "15.00", idTransformation, premierLot, date);

        TableauStatistique apresInsertion = statistiqueService.calculerTableau(
            debutAnnee,
            LocalDate.now()
        );

        assertEquals(
            avantInsertion.global().nombreTransactions() + 1,
            apresInsertion.global().nombreTransactions()
        );
        assertEquals(
            0,
            avantInsertion.global().chiffreAffaires().add(new BigDecimal("15000.00"))
                .compareTo(apresInsertion.global().chiffreAffaires())
        );
        assertEquals(
            0,
            avantInsertion.collecte().quantiteTotale().add(new BigDecimal("300.00"))
                .compareTo(apresInsertion.collecte().quantiteTotale())
        );
        assertEquals(
            0,
            avantInsertion.collecte().reductionEstimee().add(new BigDecimal("40000.00"))
                .compareTo(apresInsertion.collecte().reductionEstimee())
        );
        assertEquals(
            0,
            avantInsertion.transformation().paddyTransforme().add(new BigDecimal("300.00"))
                .compareTo(apresInsertion.transformation().paddyTransforme())
        );
        assertEquals(
            0,
            avantInsertion.transformation().produitsObtenus().add(new BigDecimal("300.00"))
                .compareTo(apresInsertion.transformation().produitsObtenus())
        );

        LigneTransactionStatistique venteTest = apresInsertion.transactions().stream()
            .filter(ligne -> ligne.type().equals("Vente test " + suffixe))
            .findFirst()
            .orElse(null);
        assertNotNull(venteTest);
        assertEquals(1, venteTest.nombre());
        assertEquals(3, venteTest.quantite());
        verifierProduit(apresInsertion, "Riz blanc test " + suffixe, "195.00");
    }

    /**
     * Vérifie le comportement professionnel du filtre : le mois actuel sert de valeur par défaut,
     * tandis qu'une période incomplète ou inversée est refusée avant toute agrégation SQL.
     */
    @Test
    void valideLaPeriodeEntreDeuxDates() {
        TableauStatistique moisActuel = statistiqueService.calculerTableau(null, null);
        assertEquals(LocalDate.now().withDayOfMonth(1), moisActuel.periode().debut());
        assertEquals(LocalDate.now(), moisActuel.periode().fin());

        assertThrows(
            IllegalArgumentException.class,
            () -> statistiqueService.calculerTableau(LocalDate.now(), null)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> statistiqueService.calculerTableau(
                LocalDate.now(),
                LocalDate.now().minusDays(1)
            )
        );
    }

    /**
     * Crée la fourniture utilisée par la transaction de test avec un prix simple permettant de
     * vérifier sans ambiguïté le calcul quantité multipliée par prix unitaire.
     */
    private Fourniture creerFourniture(String suffixe, LocalDate date) {
        Fourniture fourniture = new Fourniture();
        fourniture.setNom("Fourniture test " + suffixe);
        fourniture.setCategorie("Test statistique");
        fourniture.setQuantite(100);
        fourniture.setPrix(5000.0);
        fourniture.setDate(date);
        fourniture.setFournisseur("Test");
        return fournitureRepository.saveAndFlush(fourniture);
    }

    /**
     * Ajoute un lot de collecte et retourne son identifiant afin de pouvoir le relier aux détails
     * de transformation conformément à la colonne id_lot du schéma partagé.
     */
    private Integer insererLot(
        String reference,
        String quantite,
        String humidite,
        String prix,
        LocalDate date
    ) {
        return insererEtRetournerId(
            "INSERT INTO lot_paddy "
                + "(reference, quantite, taux_humidite, date, prix_collecte) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING id",
            reference,
            new BigDecimal(quantite),
            new BigDecimal(humidite),
            date,
            new BigDecimal(prix)
        );
    }

    /**
     * Crée un produit puis son détail de production. Cette méthode rend explicite la correspondance
     * entre une opération, le lot source et le produit obtenu.
     */
    private void insererProduitTransforme(
        String nom,
        String quantite,
        Integer idTransformation,
        Integer idLot,
        LocalDate date
    ) {
        Integer idProduit = insererEtRetournerId(
            "INSERT INTO produit (nom, prix_unitaire) VALUES (?, ?) RETURNING id",
            nom,
            BigDecimal.ZERO
        );
        baseDeDonnees.update(
            "INSERT INTO detail_lot_transforme "
                + "(id_lot_transforme, id_lot, id_produit, quantite, date) VALUES (?, ?, ?, ?, ?)",
            idTransformation,
            idLot,
            idProduit,
            new BigDecimal(quantite),
            date
        );
    }

    /**
     * Exécute un INSERT PostgreSQL avec RETURNING id. Le test conserve ainsi les vraies relations
     * entre lignes sans supposer la valeur courante d'une séquence d'identifiants.
     */
    private Integer insererEtRetournerId(String requete, Object... parametres) {
        return baseDeDonnees.queryForObject(requete, Integer.class, parametres);
    }

    /**
     * Vérifie qu'un produit précis apparaît avec la quantité réellement insérée dans les détails.
     */
    private void verifierProduit(
        TableauStatistique tableau,
        String nomProduit,
        String quantiteAttendue
    ) {
        LigneTransformationStatistique produit = tableau.transformation().repartition().stream()
            .filter(ligne -> ligne.produit().equals(nomProduit))
            .findFirst()
            .orElse(null);
        assertNotNull(produit);
        assertEquals(0, new BigDecimal(quantiteAttendue).compareTo(produit.quantite()));
    }
}
