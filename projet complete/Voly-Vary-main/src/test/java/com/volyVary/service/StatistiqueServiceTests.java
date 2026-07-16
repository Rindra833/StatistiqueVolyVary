package com.volyVary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.volyVary.dto.TableauStatistique;

class StatistiqueServiceTests {

    /**
     * Simule uniquement les réponses PostgreSQL pour vérifier l'assemblage des quatre statistiques.
     * Le test contrôle aussi que le service intégré emploie les colonnes du projet complet et une
     * borne de fin exclusive, indispensable pour inclure toutes les opérations du dernier jour.
     */
    @Test
    void calculeLeDashboardAvecLeSchemaDuProjetComplet() {
        NamedParameterJdbcTemplate baseDeDonnees = mock(NamedParameterJdbcTemplate.class);
        preparerListes(baseDeDonnees);
        preparerEntetes(baseDeDonnees);
        StatistiqueService service = new StatistiqueService(baseDeDonnees);

        TableauStatistique tableau = service.calculerTableau(
            LocalDate.of(2026, 7, 1),
            LocalDate.of(2026, 7, 15)
        );

        assertEquals(2, tableau.global().nombreTransactions());
        assertDecimal("12500.00", tableau.global().chiffreAffaires());
        assertDecimal("450.00", tableau.global().paddyCollecte());
        assertDecimal("300.00", tableau.global().paddyTransforme());
        assertDecimal("20000.00", tableau.collecte().reductionEstimee());
        assertDecimal("290.00", tableau.transformation().produitsObtenus());

        ArgumentCaptor<String> requetes = ArgumentCaptor.forClass(String.class);
        verify(baseDeDonnees, atLeastOnce()).queryForList(
            requetes.capture(),
            any(MapSqlParameterSource.class)
        );
        String toutesLesRequetes = String.join("\n", requetes.getAllValues());
        assertTrue(toutesLesRequetes.contains("SUM(t.montant_total)"));
        assertTrue(toutesLesRequetes.contains("GROUP BY id_transaction"));
        assertTrue(toutesLesRequetes.contains("date < :finExclusive"));
    }

    /**
     * Vérifie que les dates invalides sont rejetées avant tout accès à la base de données.
     */
    @Test
    void refuseUnePeriodeIncompleteOuInversee() {
        NamedParameterJdbcTemplate baseDeDonnees = mock(NamedParameterJdbcTemplate.class);
        StatistiqueService service = new StatistiqueService(baseDeDonnees);
        LocalDate aujourdHui = LocalDate.of(2026, 7, 15);

        assertThrows(
            IllegalArgumentException.class,
            () -> service.calculerTableau(aujourdHui, null)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> service.calculerTableau(aujourdHui, aujourdHui.minusDays(1))
        );
        verifyNoInteractions(baseDeDonnees);
    }

    /**
     * Fournit les listes agrégées normalement retournées par les requêtes queryForList.
     */
    private void preparerListes(NamedParameterJdbcTemplate baseDeDonnees) {
        when(baseDeDonnees.queryForList(anyString(), any(MapSqlParameterSource.class)))
            .thenAnswer(invocation -> {
                String requete = invocation.getArgument(0);
                if (requete.contains("FROM type_transaction")) {
                    return List.of(Map.<String, Object>of(
                        "type", "Vente",
                        "nombre", 2,
                        "quantite", 5,
                        "montant", new BigDecimal("12500.00")
                    ));
                }
                if (requete.contains("FROM produit")) {
                    return List.of(
                        Map.<String, Object>of(
                            "produit", "Vary",
                            "quantite", new BigDecimal("200.00")
                        ),
                        Map.<String, Object>of(
                            "produit", "Akofom-bary",
                            "quantite", new BigDecimal("90.00")
                        )
                    );
                }
                return List.of();
            });
    }

    /**
     * Fournit les deux lignes uniques utilisées pour les indicateurs de collecte et transformation.
     */
    private void preparerEntetes(NamedParameterJdbcTemplate baseDeDonnees) {
        when(baseDeDonnees.queryForMap(anyString(), any(MapSqlParameterSource.class)))
            .thenAnswer(invocation -> {
                String requete = invocation.getArgument(0);
                if (requete.contains("FROM lot_paddy lp")) {
                    return Map.<String, Object>of(
                        "nombre_lots", 3,
                        "quantite_totale", new BigDecimal("450.00"),
                        "cout_total", new BigDecimal("300000.00"),
                        "humidite_moyenne", new BigDecimal("14.50"),
                        "reduction_estimee", new BigDecimal("20000.00")
                    );
                }
                return Map.<String, Object>of(
                    "nombre_lots", 1,
                    "paddy_transforme", new BigDecimal("300.00"),
                    "cout_transformation", new BigDecimal("50000.00")
                );
            });
    }

    /**
     * Compare deux nombres sans dépendre de leur échelle BigDecimal.
     */
    private void assertDecimal(String attendu, BigDecimal obtenu) {
        assertEquals(0, new BigDecimal(attendu).compareTo(obtenu));
    }
}
