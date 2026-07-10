package com.volyVary.dto;

public class LoginRequest {
    private String nom;
    private String mdp;

    public String getNom(){
        return nom;
    }
    public void setNom(String nom){
        this.nom = nom;
    }
    public String getMdp(){
        return mdp;
    }
    public void setMdp(String mdp){
        this.mdp = mdp;
    }
}
