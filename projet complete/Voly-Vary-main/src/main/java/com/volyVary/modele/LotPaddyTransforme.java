package com.volyVary.modele;

import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "lot_paddy_transforme")
public class LotPaddyTransforme {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "reference",unique = true)
    private String reference;

    @Column(name = "quantite")
    private double quantite;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "prix_transformation")
    private double prixTransformation;
    
    @OneToMany(mappedBy = "lotTransforme", cascade = CascadeType.ALL)
    private List<DetailLotTransforme> detailLotTransforme;

    public double getPrixTransformation() {
        return prixTransformation;
    }

    public void setPrixTransformation(double prixTransformation) {
        this.prixTransformation = prixTransformation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}