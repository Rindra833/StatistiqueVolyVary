package com.volyVary.dto;

import java.math.BigDecimal;

public record LigneTransformationStatistique(
    String produit,
    BigDecimal quantite,
    BigDecimal pourcentage
) {
}
