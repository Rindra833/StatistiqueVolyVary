package com.volyVary.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.volyVary.modele.*;
import com.volyVary.repository.*;

@Service
public class FournitureCatalogService {
    @Autowired
    private CategorieFournitureRepository categorieFournitureRepository;

    @Autowired
    private FournitureRepository fournitureRepository;

    @Autowired
    private TypeTransactionRepository typeTransactionRepository;

    public List<CategorieFourniture> getCategories() {
        return categorieFournitureRepository.findAllByOrderByLibelleAsc();
    }

    public List<Fourniture> getFournituresParCategorie(int idCategorie) {
        return fournitureRepository.findByCategory(idCategorie);
    }

    public List<TypeTransaction> getTypesTransaction() {
        return typeTransactionRepository.findAllByOrderByLibelleAsc();
    }

    public TypeTransaction trouverTypeTransaction(int idTypeTransaction) {
        return typeTransactionRepository.findById(idTypeTransaction).orElse(null);
    }

    public CategorieFourniture trouverCategorie(int idCategorie) {
        return categorieFournitureRepository.findById(idCategorie).orElse(null);
    }

    public List<Fourniture> trouverFournituresSelectionnees(int idCategorie, List<Integer> idFournitures) {
        List<Fourniture> fournituresCategorie = getFournituresParCategorie(idCategorie);
        List<Fourniture> selectionnees = new ArrayList<>();
        for (int idFourniture : idFournitures) {
            for (Fourniture f : fournituresCategorie) {
                if (f.getId() == idFourniture) {
                    selectionnees.add(f);
                    break;
                }
            }
        }
        return selectionnees;
    }

    public Fourniture trouverFournitureParId(int id) {
        return fournitureRepository.findById(id).orElse(null);
    }

    public Fourniture trouverFournitureParReference(String reference) {
        for (Fourniture f : fournitureRepository.findAll()) {
            if (f.getReference().equals(reference)) {
                return f;
            }
        }
        return null;
    }

    public Map<Integer, Fourniture> construireMapFournituresParReference() {
        Map<Integer, Fourniture> map = new HashMap<>();
        for (Fourniture f : fournitureRepository.findAll()) {
            map.put(f.getId(), f);
        }
        return map;
    }

    public Map<String, Fourniture> construireMapFournituresParRef() {
        Map<String, Fourniture> map = new HashMap<>();
        for (Fourniture f : fournitureRepository.findAll()) {
            map.put(f.getReference(), f);
        }
        return map;
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

    public List<Map<String, Object>> convertirFournitures(List<Fourniture> fournitures) {
        List<Map<String, Object>> resultat = new ArrayList<>();
        for (Fourniture f : fournitures) {
            Map<String, Object> ligne = new HashMap<>();
            ligne.put("idFourniture", f.getId());
            ligne.put("referenceFourniture", f.getReference());
            ligne.put("prixUnitaire", f.getPrixUnitaire());
            resultat.add(ligne);
        }
        return resultat;
    }
}
