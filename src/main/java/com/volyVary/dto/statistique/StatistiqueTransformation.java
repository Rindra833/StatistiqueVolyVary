package com.volyVary.dto.statistique;

import java.math.BigDecimal;
import java.util.List;

/**
 * Regroupe les indicateurs des opérations de transformation de paddy.
 */
public record StatistiqueTransformation(
    long nombreLots,
    BigDecimal paddyTransforme,
    BigDecimal coutTransformation,
    BigDecimal produitsObtenus,
    List<LigneTransformationStatistique> repartition
) {
}
