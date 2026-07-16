package com.volyVary.modele;

import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "distribution")
public class Distribution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "reference")
    private String reference;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "id_client")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "id_lieu")
    private Lieu lieu;

    @ManyToOne
    @JoinColumn(name = "id_livreur")
    private Livreur livreur;

    @OneToMany(mappedBy = "distribution" ,cascade =CascadeType.ALL)
    private List<DetailDistribution> details;

    @OneToMany(mappedBy = "distribution" ,cascade =CascadeType.ALL)
    private List<HistoriqueDistribution> historiques;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }

    public Lieu getLieu() {
        return lieu;
    }
    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public Livreur getLivreur() {
        return livreur;
    }
    public void setLivreur(Livreur livreur) {
        this.livreur = livreur;
    }
    public List<DetailDistribution> getDetails() {
        return details;
    }
    public void setDetails(List<DetailDistribution> details) {
        this.details = details;
    }
    public List<HistoriqueDistribution> getHistoriques() {
        return historiques;
    }
    public void setHistoriques(List<HistoriqueDistribution> historiques) {
        this.historiques = historiques;
    }
}