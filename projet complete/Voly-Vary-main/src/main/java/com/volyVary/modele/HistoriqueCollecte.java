package com.volyVary.modele;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "historique_collecte")
public class HistoriqueCollecte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int idHistoriqueCollecte;

    @ManyToOne
    @JoinColumn(name = "id_client")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "id_lot_paddy")
    private LotPaddy lotPaddy;

    @ManyToOne
    @JoinColumn(name = "id_statut")
    private StatutLotPaddy statut;

    @Column(name = "date")
    private LocalDate dateHistoriqueCollecte;

    public HistoriqueCollecte() {}
    
    public int getIdHistoriqueCollecte() {
        return idHistoriqueCollecte;
    }
    public void setIdHistoriqueCollecte(int idHistoriqueCollecte) {
        this.idHistoriqueCollecte = idHistoriqueCollecte;
    }

    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }

    public LotPaddy getLotPaddy() {
        return lotPaddy;
    }
    public void setLotPaddy(LotPaddy lotPaddy) {
        this.lotPaddy = lotPaddy;
    }

    public StatutLotPaddy getStatut() {
        return statut;
    }
    public void setStatut(StatutLotPaddy statut) {
        this.statut = statut;
    }

    public LocalDate getDateHistoriqueCollecte() {
        return dateHistoriqueCollecte;
    }
    public void setDateHistoriqueCollecte(LocalDate dateHistoriqueCollecte) {
        this.dateHistoriqueCollecte = dateHistoriqueCollecte;
    }
}