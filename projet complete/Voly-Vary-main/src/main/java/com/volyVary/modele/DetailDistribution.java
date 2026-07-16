package com.volyVary.modele;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "detail_distribution")
public class DetailDistribution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_distribution")
    private Distribution distribution;

    @ManyToOne
    @JoinColumn(name = "id_produit")
    private Produit produit;

    @Column(name = "quantite")
    private Double quantite;

    @Column(name = "date")
    private LocalDate date;

    public DetailDistribution() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Distribution getDistribution() {
        return distribution;
    }

    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public Double getQuantite() {
        return quantite;
    }

    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}