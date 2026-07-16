package com.volyVary.modele;

import jakarta.persistence.*;

@Entity
@Table(name = "type_transaction")
public class TypeTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "libelle")
    private String libelle;

    /*Constructeur*/
    public TypeTransaction(){

    }

    /*Setters*/
    public void setId(int id) {
        this.id = id;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    /*Getters*/
    public int getId() {
        return id;
    }

    public String getLibelle() {
        return libelle;
    }
 
}