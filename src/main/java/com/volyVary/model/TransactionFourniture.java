package com.volyVary.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Représente l'en-tête d'une transaction de fournitures. Le nom SQL et les
 * colonnes suivent exactement le schéma partagé dans instructions.pdf.
 */
@Entity
@Table(name = "transaction")
public class TransactionFourniture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_type", nullable = false)
    private TypeTransaction type;

    @Column(nullable = false, length = 100)
    private String reference;

    @Column(nullable = false)
    private LocalDate date;
}
