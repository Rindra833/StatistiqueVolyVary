package com.volyVary.modele;

import jakarta.persistence.*;

@Entity
@Table(name = "livreur")
public class Livreur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "matricule_vehicule")
    private String matriculeVehicule;

    public Livreur() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMatriculeVehicule() {
        return matriculeVehicule;
    }

    public void setMatriculeVehicule(String matriculeVehicule) {
        this.matriculeVehicule = matriculeVehicule;
    }
}