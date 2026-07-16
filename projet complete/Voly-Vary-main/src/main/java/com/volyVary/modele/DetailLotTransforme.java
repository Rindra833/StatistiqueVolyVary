package com.volyVary.modele;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "detail_lot_transforme")
public class DetailLotTransforme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_produit")
    private Produit Produit;

    @ManyToOne
    @JoinColumn(name = "id_lot_transforme")
    private LotPaddyTransforme lotTransforme;

    @ManyToOne
    @JoinColumn(name = "id_lot", nullable = false)
    private LotPaddy lotPaddy;

    @Column(name = "quantite")
    private double quantite;

    @Column(name = "date")
    private LocalDateTime date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Produit getProduit() {
        return Produit;
    }

    public void setProduit(Produit produit) {
        Produit = produit;
    }

    public LotPaddyTransforme getLotTransforme() {
        return lotTransforme;
    }

    public void setLotTransforme(LotPaddyTransforme lotTransforme) {
        this.lotTransforme = lotTransforme;
    }

    public LotPaddy getLotPaddy() {
        return lotPaddy;
    }

    public void setLotPaddy(LotPaddy lotPaddy) {
        this.lotPaddy = lotPaddy;
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
