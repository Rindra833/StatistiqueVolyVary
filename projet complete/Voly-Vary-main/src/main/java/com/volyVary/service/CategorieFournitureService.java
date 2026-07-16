package com.volyVary.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.volyVary.modele.*;
import com.volyVary.repository.*;

@Service
public class CategorieFournitureService {

    @Autowired
    private CategorieFournitureRepository categorieFournitureRepository;

    @Autowired
    private FournitureRepository fournitureRepository;

    /**
     * Retourne toutes les catégories dans l'ordre alphabétique. Cette méthode reste utilisée par
     * les formulaires Transaction et Fourniture pour alimenter leurs listes déroulantes.
     */
    public List<CategorieFourniture> getCategories() {
        return categorieFournitureRepository.findAllByOrderByLibelleAsc();
    }

    /**
     * Recherche une catégorie par identifiant pour les services métier historiques. Une absence
     * produit volontairement null afin de conserver le contrat déjà utilisé par ces services.
     */
    public CategorieFourniture trouverCategorie(int idCategorie) {
        return categorieFournitureRepository.findById(idCategorie).orElse(null);
    }

    /**
     * Filtre le référentiel administratif par libellé sans tenir compte des majuscules.
     */
    @Transactional(readOnly = true)
    public List<CategorieFourniture> lister(String recherche) {
        String texte = normaliser(recherche);
        return getCategories().stream()
            .filter(categorie -> texte.isEmpty()
                || normaliser(categorie.getLibelle()).contains(texte))
            .toList();
    }

    /**
     * Charge obligatoirement la catégorie demandée par le formulaire de modification. Le message
     * explicite évite une erreur JSP difficile à comprendre lorsque l'identifiant n'existe plus.
     */
    @Transactional(readOnly = true)
    public CategorieFourniture obtenir(Integer id) {
        return categorieFournitureRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Catégorie de fourniture introuvable : " + id
            ));
    }

    /**
     * Crée ou modifie une catégorie après nettoyage du libellé. Deux catégories ne peuvent pas
     * porter le même nom, même avec une casse différente, afin de garder un référentiel cohérent.
     */
    @Transactional
    public CategorieFourniture enregistrer(Integer id, String libelle) {
        String libelleNettoye = libelle == null ? "" : libelle.trim();
        if (libelleNettoye.isEmpty()) {
            throw new IllegalArgumentException("Le libellé de la catégorie est obligatoire.");
        }

        CategorieFourniture memeLibelle = categorieFournitureRepository
            .findFirstByLibelleIgnoreCase(libelleNettoye)
            .orElse(null);
        if (memeLibelle != null && (id == null || memeLibelle.getId() != id)) {
            throw new IllegalArgumentException("Cette catégorie existe déjà.");
        }

        CategorieFourniture categorie = id == null
            ? new CategorieFourniture()
            : obtenir(id);
        categorie.setLibelle(libelleNettoye);
        return categorieFournitureRepository.save(categorie);
    }

    /**
     * Supprime uniquement une catégorie encore inutilisée. Une fourniture conserve son identifiant
     * de catégorie : autoriser la suppression casserait son affichage et le formulaire Transaction.
     */
    @Transactional
    public void supprimer(Integer id) {
        obtenir(id);
        if (fournitureRepository.existsByIdCategorie(id)) {
            throw new IllegalArgumentException(
                "Cette catégorie est utilisée par une fourniture et ne peut pas être supprimée."
            );
        }
        categorieFournitureRepository.deleteById(id);
    }


    public Map<String, Object> convertirCategorie(CategorieFourniture categorieFourniture) {
        Map<String, Object> resultat = new HashMap<>();
        resultat.put("idCategorieFourniture", categorieFourniture.getId());
        resultat.put("libelleCategorieFourniture", categorieFourniture.getLibelle());
        return resultat;
    }

    public List<Map<String, Object>> convertirCategories(List<CategorieFourniture> categories) {
        List<Map<String, Object>> resultat = new ArrayList<>();
        for (CategorieFourniture categorie : categories) {
            resultat.add(convertirCategorie(categorie));
        }
        return resultat;
    }

    /** Normalise les valeurs utilisées par la recherche et la comparaison des libellés. */
    private String normaliser(String valeur) {
        return valeur == null ? "" : valeur.trim().toLowerCase();
    }
}
