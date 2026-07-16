package com.volyVary.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.volyVary.modele.*;
import com.volyVary.repository.*;

@Service
public class FournitureService {

    @Autowired
    private FournitureRepository fournitureRepository;

    /**
     * Retourne les fournitures correspondant à la référence et, si elle est renseignée, à la
     * catégorie sélectionnée. Le filtrage emploie uniquement les attributs du modèle Fourniture
     * appartenant au projet complet.
     */
    public List<Fourniture> lister(String recherche, Integer idCategorie) {
        String texteRecherche = normaliser(recherche);

        return fournitureRepository.findAll().stream()
            .filter(fourniture -> texteRecherche.isEmpty()
                || normaliser(fourniture.getReference()).contains(texteRecherche))
            .filter(fourniture -> idCategorie == null
                || fourniture.getIdCategorie() == idCategorie)
            .sorted(Comparator.comparing(Fourniture::getReference, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }
 
    public Fourniture obtenir(Integer id) {
        return fournitureRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Fourniture introuvable : " + id));
    }
  
    public void enregistrer(Integer id, Fourniture donnees) {
        Fourniture fourniture = id == null ? new Fourniture() : obtenir(id);
        fourniture.setIdCategorie(donnees.getIdCategorie());
        fourniture.setPrixUnitaire(donnees.getPrixUnitaire());
        fourniture.setReference(donnees.getReference());
        
        fournitureRepository.save(fourniture);
    }

    public void supprimer(Integer id) {
        if (fournitureRepository.existsById(id)) {
            fournitureRepository.deleteById(id);
        }
    }

    private String normaliser(String valeur) {
        return valeur == null ? "" : valeur.trim().toLowerCase();
    }
}
