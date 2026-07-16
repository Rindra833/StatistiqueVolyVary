package com.volyVary.dto;

import java.math.BigDecimal;
import java.util.List;

public record StatistiqueTransformation(
    long nombreLots,
    BigDecimal paddyTransforme,
    BigDecimal coutTransformation,
    BigDecimal produitsObtenus,
    List<LigneTransformationStatistique> repartition
) {
}
