package com.volyVary.dto;

public class LotStockDto {
    private int id;
    private int idLot;
    private String reference;
    private double quantiteReel;
    public LotStockDto(int idLot,double quantiteReel , String reference) {
        this.idLot = idLot;
        this.quantiteReel = quantiteReel;
        this.reference = reference;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getReference() {
        return reference;
    }

    public int getIdLot() {
        return idLot;
    }
    public void setIdLot(int idLot) {
        this.idLot = idLot;
    }
    public double getQuantiteReel() {
        return quantiteReel;
    }
    public void setQuantiteReel(double quantiteReel) {
        this.quantiteReel = quantiteReel;
    }
   
}
