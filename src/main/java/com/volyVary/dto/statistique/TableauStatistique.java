package com.volyVary.dto.statistique;

import java.util.List;

/**
 * Objet unique remis au contrôleur pour rendre toute la page statistique.
 */
public record TableauStatistique(
    PeriodeStatistique periode,
    ResumeGlobalStatistique global,
    List<LigneTransactionStatistique> transactions,
    StatistiqueCollecte collecte,
    StatistiqueTransformation transformation,
    List<PointMensuelStatistique> evolution
) {
}
