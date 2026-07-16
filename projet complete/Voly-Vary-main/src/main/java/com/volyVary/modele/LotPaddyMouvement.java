package com.volyVary.modele;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "lot_paddy_mouvement")
public class LotPaddyMouvement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_lot_paddy")
    private LotPaddy lotPaddy;

    @ManyToOne
    @JoinColumn(name = "id_lot_transforme")
    private LotPaddyTransforme lotPaddyTransforme;

    @Column(name ="quantite")
    private double quantite;

    @Column(name = "date")
    private LocalDateTime date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LotPaddy getLotPaddy() {
        return lotPaddy;
    }

    public void setLotPaddy(LotPaddy lotPaddy) {
        this.lotPaddy = lotPaddy;
    }

    public LotPaddyTransforme getLotPaddyTransforme() {
        return lotPaddyTransforme;
    }

    public void setLotPaddyTransforme(LotPaddyTransforme lotPaddyTransforme) {
        this.lotPaddyTransforme = lotPaddyTransforme;
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
