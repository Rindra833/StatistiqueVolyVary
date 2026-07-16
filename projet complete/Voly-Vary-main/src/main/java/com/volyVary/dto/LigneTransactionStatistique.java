package com.volyVary.dto;

import java.math.BigDecimal;

public record LigneTransactionStatistique(
    String type,
    long nombre,
    long quantite,
    BigDecimal montant
) {
}
