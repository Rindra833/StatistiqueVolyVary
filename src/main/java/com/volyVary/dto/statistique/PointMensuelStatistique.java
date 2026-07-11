package com.volyVary.dto.statistique;

import java.math.BigDecimal;

/**
 * Contient les valeurs et largeurs visuelles d'un mois de l'évolution globale.
 */
public record PointMensuelStatistique(
    String mois,
    long transactions,
    BigDecimal collecte,
    BigDecimal transformation,
    int largeurTransactions,
    int largeurCollecte,
    int largeurTransformation
) {
}
