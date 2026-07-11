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
 * Représente une opération ayant transformé une quantité de paddy.
 */
@Entity
@Table(name = "lot_paddy_transforme")
public class LotPaddyTransforme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String reference;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal quantite;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "prix_transformation", nullable = false, precision = 18, scale = 2)
    private BigDecimal prixTransformation;
}
