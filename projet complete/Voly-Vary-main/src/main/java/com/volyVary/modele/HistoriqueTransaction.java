package com.volyVary.modele;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historique_transaction")
public class HistoriqueTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "id_transaction")
    private int idTransaction;

    @Column(name = "id_statut")
    private int idStatut;

    @Column(name = "date")
    private LocalDateTime date;

    public HistoriqueTransaction() {
        
    }

    public HistoriqueTransaction(int id, int idTransaction, int idStatut, LocalDateTime date) {
        this.id = id;
        this.idTransaction = idTransaction;
        this.idStatut = idStatut;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(int idTransaction) {
        this.idTransaction = idTransaction;
    }

    public int getIdStatut() {
        return idStatut;
    }

    public void setIdStatut(int idStatut) {
        this.idStatut = idStatut;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}