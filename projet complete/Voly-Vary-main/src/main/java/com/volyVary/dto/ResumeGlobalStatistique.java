package com.volyVary.dto;

import java.math.BigDecimal;

public record ResumeGlobalStatistique(
    long nombreTransactions,
    BigDecimal chiffreAffaires,
    BigDecimal paddyCollecte,
    BigDecimal paddyTransforme
) {
}
