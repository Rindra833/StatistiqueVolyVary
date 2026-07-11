package com.volyVary.dto.statistique;

import java.math.BigDecimal;

/**
 * Présente les quatre valeurs principales du tableau de bord global.
 */
public record ResumeGlobalStatistique(
    long nombreTransactions,
    BigDecimal chiffreAffaires,
    BigDecimal paddyCollecte,
    BigDecimal paddyTransforme
) {
}
