package com.volyVary.modele;

import jakarta.persistence.*;

@Entity
@Table(name = "fourniture")
public class Fourniture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "id_categorie")
    private int idCategorie;

    @Column(name = "reference")
    private String reference;

    @Column(name = "prix_unitaire")
    private double prixUnitaire;

    public Fourniture() {
    }

    public Fourniture(int id, int idCategorie, String reference, double prixUnitaire) {
        this.id = id;
        this.idCategorie = idCategorie;
        this.reference = reference;
        this.prixUnitaire = prixUnitaire;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }
}