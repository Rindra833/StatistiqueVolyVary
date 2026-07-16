package com.volyVary.modele;

import jakarta.persistence.*;

@Entity
@Table(name = "detail_transaction")
public class DetailTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "id_transaction")
    private int idTransaction;

    @Column(name = "id_fourniture")
    private int idFourniture;

    @Column(name = "quantite")
    private int quantite;

    @Column(name = "montant_ligne")
    private double montantLigne;

    public DetailTransaction() {
        
    }

    public DetailTransaction(int id, int idTransaction, int idFourniture, int quantite, double montantLigne) {
        this.id = id;
        this.idTransaction = idTransaction;
        this.idFourniture = idFourniture;
        this.quantite = quantite;
        this.montantLigne = montantLigne;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(int idTransaction) {
        this.idTransaction = idTransaction;
    }

    public int getIdFourniture() {
        return idFourniture;
    }

    public void setIdFourniture(int idFourniture) {
        this.idFourniture = idFourniture;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getMontantLigne() {
        return montantLigne;
    }

    public void setMontantLigne(double montantLigne) {
        this.montantLigne = montantLigne;
    }
}