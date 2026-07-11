package com.volyVary.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Représente un lot de paddy reçu par le module de collecte.
 */
@Entity
@Table(name = "lot_paddy")
public class LotPaddy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String reference;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal quantite;

    @Column(name = "taux_humidite", nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxHumidite;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "prix_collecte", nullable = false, precision = 18, scale = 2)
    private BigDecimal prixCollecte;
}
