package com.volyVary.dto;

import java.math.BigDecimal;

public record StatistiqueCollecte(
    long nombreLots,
    BigDecimal quantiteTotale,
    BigDecimal coutTotal,
    BigDecimal humiditeMoyenne,
    BigDecimal reductionEstimee
) {
}
