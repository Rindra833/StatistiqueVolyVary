package com.volyVary.modele;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "lot_paddy")
public class LotPaddy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int idLotPaddy;

    @Column(name = "reference")
    private String reference;

    @Column(name = "quantite")
    private Double quantite;

    @Column(name = "taux_humidite")
    private Double tauxhumidite;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "prix_collecte")
    private Double prixCollecte;

    @ManyToOne
    @JoinColumn(name = "id_collecte")
    private Collecte collecte;

    @OneToMany(mappedBy = "lotPaddy")
    private List<HistoriqueCollecte> historiquesLotPaddy;

    public LotPaddy() {}

    public int getIdLotPaddy() {
        return idLotPaddy;
    }
    public void setIdLotPaddy(int idLotPaddy) {
        this.idLotPaddy = idLotPaddy;
    }

    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }

    public Double getQuantite() {
        return quantite;
    }
    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }

    public Double getTauxHumidite() {
        return tauxhumidite;
    }
    public void setTauxHumidite(Double tauxhumidite) {
        this.tauxhumidite = tauxhumidite;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getPrixCollecte() {
        return prixCollecte;
    }
    public void setPrixCollecte(Double prixCollecte) {
        this.prixCollecte = prixCollecte;
    }

    public Collecte getCollecte() {
        return collecte;
    }
    public void setCollecte(Collecte collecte) {
        this.collecte = collecte;
    }

    public List<HistoriqueCollecte> getHistoriqueLotPaddy() {
        return historiquesLotPaddy;
    }
    public void setHistoriqueLotPaddy(List<HistoriqueCollecte> historiquesLotPaddy) {
        this.historiquesLotPaddy = historiquesLotPaddy;
    }
}