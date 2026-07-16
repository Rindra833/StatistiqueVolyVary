package com.volyVary.dto;

import java.math.BigDecimal;

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
