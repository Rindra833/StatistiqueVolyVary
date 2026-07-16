package com.volyVary.modele;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "historique_distribution")
public class HistoriqueDistribution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_distribution")
    private Distribution distribution;

    @ManyToOne
    @JoinColumn(name = "id_statut_distribution")
    private StatutDistribution statut;

    @Column(name = "date")
    private LocalDate date;

    public HistoriqueDistribution(){

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

    public StatutDistribution getStatut() {
        return statut;
    }

    public void setStatut(StatutDistribution statut) {
        this.statut = statut;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
