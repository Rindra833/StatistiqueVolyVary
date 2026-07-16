package com.volyVary.modele;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "id_client")
    private int idClient;

    @Column(name = "id_type")
    private int idType;

    @Column(name = "reference")
    private String reference;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "montant_total")
    private double montantTotal;

    /*Constructeur*/
    public Transaction() {

    }

    public Transaction(int id, int idClient, int idType, String reference, LocalDateTime date, double montantTotal) {
        this.id = id;
        this.idClient = idClient;
        this.idType = idType;
        this.reference = reference;
        this.date = date;
        this.montantTotal = montantTotal;
    }


    /*Setters*/
    public void setId(int id) {
        this.id = id;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    /*Getters*/
    public int getId() {
        return id;
    }

    public int getIdType() {
        return idType;
    }

    public String getReference() {
        return reference;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getIdClient() {
        return idClient;
    }

    public double getMontantTotal() {
        return montantTotal;
    }
}