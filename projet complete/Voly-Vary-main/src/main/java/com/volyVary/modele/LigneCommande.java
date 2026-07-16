package com.volyVary.modele;

public class LigneCommande {

    private Integer idProduit;
    private Double quantite;

    public LigneCommande() {
    }

    public LigneCommande(Integer idProduit, Double quantite) {
        this.idProduit = idProduit;
        this.quantite = quantite;
    }

    public Integer getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(Integer idProduit) {
        this.idProduit = idProduit;
    }

    public Double getQuantite() {
        return quantite;
    }

    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }
}
