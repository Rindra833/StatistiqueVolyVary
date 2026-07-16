package com.volyVary.modele;

import jakarta.persistence.*;

@Entity
@Table(name = "commune")
public class Commune {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nom")
    private String nom;

    @ManyToOne
    @JoinColumn(name = "id_region")
    private Region region;


    public Commune(){
        
    }

    public Integer getId() { 
        return id; 
    }
    public void setId(Integer id) { 
        this.id = id; 
    }

    public String getNom() { 
        return nom;
    }
    
    public void setNom(String nom) { 
        this.nom = nom; 
    }

    public Region getRegion() { 
        return region; 
    }

    public void setRegion(Region region) { 
        this.region = region; 
    }
}