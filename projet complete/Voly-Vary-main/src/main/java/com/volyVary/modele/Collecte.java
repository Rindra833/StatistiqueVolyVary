package com.volyVary.modele;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "collecte")
public class Collecte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int idCollecte;

    @Column(name = "prix_unitaire")
    private Double prixUnitaire;

    @OneToMany(mappedBy = "collecte")
    private List<LotPaddy> lots;

    public Collecte() {}

    public int getIdCollecte(){
        return idCollecte;
    }
    public void setIdCollecte(int idCollecte){
        this.idCollecte = idCollecte;
    }

    public Double getPrixUnitaire(){
        return prixUnitaire;
    }
    public void setPrixUnitaire(Double prixUnitaire){
        this.prixUnitaire = prixUnitaire;
    }

    public List<LotPaddy> getLots(){
        return lots;
    }
    public void setLots(List<LotPaddy> lots){
        this.lots = lots;
    }
}