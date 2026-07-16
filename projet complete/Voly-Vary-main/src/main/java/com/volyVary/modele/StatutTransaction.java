package com.volyVary.modele;

import jakarta.persistence.*;

@Entity
@Table(name = "statut_transaction")
public class StatutTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "libelle")
    private String libelle;

    @Column(name = "sigle")
    private String sigle;

    public StatutTransaction() {
    }

    public StatutTransaction(int id, String libelle, String sigle) {
        this.id = id;
        this.libelle = libelle;
        this.sigle = sigle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getSigle() {
        return sigle;
    }

    public void setSigle(String sigle) {
        this.sigle = sigle;
    }
}