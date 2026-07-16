package com.volyVary.dto;

import java.util.List;

public record TableauStatistique(
    PeriodeStatistique periode,
    ResumeGlobalStatistique global,
    List<LigneTransactionStatistique> transactions,
    StatistiqueCollecte collecte,
    StatistiqueTransformation transformation,
    List<PointMensuelStatistique> evolution
) {
}
