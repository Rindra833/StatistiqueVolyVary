package com.volyVary.modele;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "statut_lot_paddy")
public class StatutLotPaddy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id")
    private int idStatutLotPaddy;

    @Column(name = "libelle")
    private String libelle;

    @Column(name = "sigle")
    private String sigle;
    
    @OneToMany(mappedBy = "statut")
    private List<HistoriqueCollecte> historiqueStatutLotPaddy;

    public StatutLotPaddy() {}

    public int getIdStatutLotPaddy(){
        return idStatutLotPaddy;
    }
    public void setIdStatutLotPaddy(int idStatutLotPaddy){
        this.idStatutLotPaddy = idStatutLotPaddy;
    }

    public String getLibelle(){
        return libelle;
    }
    public void setLibelle(String libelle){
        this.libelle = libelle;
    }

    public String getSigle(){
        return sigle;
    }
    public void setSigle(String sigle){
        this.sigle = sigle;
    }

    public List<HistoriqueCollecte> getHistoriqueStatutLotPaddy() {
        return historiqueStatutLotPaddy;
    }
    public void setHistoriqueStatutLotPaddy(List<HistoriqueCollecte> historiqueStatutLotPaddy) {
        this.historiqueStatutLotPaddy = historiqueStatutLotPaddy;
    }
}