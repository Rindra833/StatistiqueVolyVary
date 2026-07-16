package com.volyVary.dto;

import java.time.LocalDate;

public record PeriodeStatistique(
    String code,
    String libelle,
    LocalDate debut,
    LocalDate fin
) {
}
