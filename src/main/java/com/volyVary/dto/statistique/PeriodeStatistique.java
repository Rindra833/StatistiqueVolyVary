package com.volyVary.dto.statistique;

import java.time.LocalDate;

/**
 * Décrit la période réellement appliquée aux calculs statistiques.
 */
public record PeriodeStatistique(
    String code,
    String libelle,
    LocalDate debut,
    LocalDate fin
) {
}
