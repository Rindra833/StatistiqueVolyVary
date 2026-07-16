package com.volyVary.modele;

import jakarta.persistence.*;

@Entity
@Table(name = "reduction")
public class Reduction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int idReduction;

    @Column(name = "humidite1")
    private Double humidite1;

    @Column(name = "humidite2")
    private Double humidite2;

    @Column(name = "reduction")
    private Double reduction;

    public Reduction() {}

    public int getIdReduction() {
        return idReduction;
    }
    public void setIdReduction(int idReduction) {
        this.idReduction = idReduction;
    }

    public Double getHumidite1() {
        return humidite1;
    }
    public void setHumidite1(Double humidite1) {
        this.humidite1 = humidite1;
    }

    public Double getHumidite2() {
        return humidite2;
    }
    public void setHumidite2(Double humidite2) {
        this.humidite2 = humidite2;
    }

    public Double getReduction() {
        return reduction;
    }
    public void setReduction(Double reduction) {
        this.reduction = reduction;
    }
}