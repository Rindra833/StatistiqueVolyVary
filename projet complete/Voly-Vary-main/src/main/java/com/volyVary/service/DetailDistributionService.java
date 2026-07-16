package com.volyVary.service;

import com.volyVary.modele.*;
import com.volyVary.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DetailDistributionService {

    @Autowired
    private DetailDistributionRepository detailRepo;

    @Autowired
    private ProduitRepository produitRepo;

    @Transactional
    public void sauvegarderDetails(Distribution distribution, List<LigneCommande> lignes) {
        for (LigneCommande ligne : lignes) {
            DetailDistribution detail = new DetailDistribution();
            detail.setDistribution(distribution);
            detail.setProduit(produitRepo.findById(ligne.getIdProduit()).orElseThrow());
            detail.setQuantite(ligne.getQuantite());
            detail.setDate(LocalDate.now());
            detailRepo.save(detail);
        }
    }
}
