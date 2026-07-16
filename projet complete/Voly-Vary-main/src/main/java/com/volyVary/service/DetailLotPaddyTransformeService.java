package com.volyVary.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.volyVary.modele.*;
import com.volyVary.repository.*;

@Service
public class DetailLotPaddyTransformeService {
    @Autowired
    private DetailLotTransformeRepository detailLotTransformeRepository;

    @Autowired
    private ProduitRepository produitRepository;

    public List<DetailLotTransforme> getDetailsLotTransforme(int idLotTransforme) {
        return detailLotTransformeRepository.findByLotTransformeId(idLotTransforme);
    }

    /**
     * Répartit les produits obtenus pour chaque lot d'origine réellement consommé. Cette structure
     * renseigne la colonne obligatoire detail_lot_transforme.id_lot et conserve la traçabilité
     * lorsque la transformation utilise plusieurs lots de paddy.
     */
    public void insererDetailsProduits(
        LotPaddyTransforme lotPaddyTransforme,
        List<LotPaddyMouvement> mouvements
    ) {
        List<Produit> listeProduit = produitRepository.findAll();
        for (LotPaddyMouvement mouvement : mouvements) {
            for (Produit produit : listeProduit) {
                DetailLotTransforme detail = new DetailLotTransforme();
                detail.setProduit(produit);
                detail.setLotTransforme(lotPaddyTransforme);
                detail.setLotPaddy(mouvement.getLotPaddy());
                detail.setQuantite(
                    (produit.getRendement() * mouvement.getQuantite()) / 100
                );
                detail.setDate(lotPaddyTransforme.getDate());
                detailLotTransformeRepository.save(detail);
            }
        }
    }

}
