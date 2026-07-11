package com.volyVary.dto.statistique;

import java.math.BigDecimal;

/**
 * Regroupe les indicateurs calculés sur les lots de paddy collectés.
 */
public record StatistiqueCollecte(
    long nombreLots,
    BigDecimal quantiteTotale,
    BigDecimal coutTotal,
    BigDecimal humiditeMoyenne,
    BigDecimal reductionEstimee
) {
}
