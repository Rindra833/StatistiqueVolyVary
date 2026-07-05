package com.Hasner.VolyVary.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "mouvement_projet")
public class MouvementProjet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projet_id", nullable = false)
    private Projet projet;

    @Column(name = "date_operation", nullable = false)
    private LocalDate dateOperation;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NatureMouvement nature;

    @Column(nullable = false)
    private String libelle;

    public MouvementProjet() {
    }

    public MouvementProjet(Projet projet, LocalDate dateOperation, BigDecimal montant, NatureMouvement nature, String libelle) {
        this.projet = projet;
        this.dateOperation = dateOperation;
        this.montant = montant;
        this.nature = nature;
        this.libelle = libelle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Projet getProjet() {
        return projet;
    }

    public void setProjet(Projet projet) {
        this.projet = projet;
    }

    public LocalDate getDateOperation() {
        return dateOperation;
    }

    public void setDateOperation(LocalDate dateOperation) {
        this.dateOperation = dateOperation;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public NatureMouvement getNature() {
        return nature;
    }

    public void setNature(NatureMouvement nature) {
        this.nature = nature;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}
