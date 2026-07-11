package com.volyVary.dto.statistique;

import java.math.BigDecimal;

/**
 * Regroupe la quantité et la part d'un produit issu de la transformation.
 */
public record LigneTransformationStatistique(
    String produit,
    BigDecimal quantite,
    BigDecimal pourcentage
) {
}
