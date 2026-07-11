package com.volyVary.dto.statistique;

import java.math.BigDecimal;

/**
 * Regroupe les résultats d'un type de transaction de fournitures.
 */
public record LigneTransactionStatistique(
    String type,
    long nombre,
    long quantite,
    BigDecimal montant
) {
}
