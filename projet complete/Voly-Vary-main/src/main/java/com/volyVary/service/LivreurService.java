package com.volyVary.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.volyVary.modele.Livreur;
import com.volyVary.repository.DistributionRepository;
import com.volyVary.repository.LivreurRepository;

@Service
public class LivreurService {

    @Autowired
    private LivreurRepository livreurRepository;

    @Autowired
    private DistributionRepository distributionRepository;

    /**
     * Retourne les livreurs dont le matricule contient le texte recherché. La normalisation rend la
     * recherche insensible aux majuscules et protège aussi contre une valeur nulle.
     */
    public List<Livreur> lister(String recherche) {
        String texteRecherche = normaliser(recherche);
        return livreurRepository.findAll().stream()
            .filter(livreur -> texteRecherche.isEmpty()
                || normaliser(livreur.getMatriculeVehicule()).contains(texteRecherche))
            .sorted(Comparator.comparing(
                Livreur::getMatriculeVehicule,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            ))
            .toList();
    }

    /**
     * Charge un livreur existant ou signale clairement que l'identifiant est inconnu.
     */
    public Livreur obtenir(Integer id) {
        return livreurRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Livreur introuvable : " + id));
    }

    /**
     * Crée ou modifie un livreur en ne copiant que l'attribut défini par le modèle commun. Le
     * contrôle empêche l'insertion d'un matricule vide, inutilisable lors d'une distribution.
     */
    public void enregistrer(Integer id, Livreur donnees) {
        Livreur livreur = id == null ? new Livreur() : obtenir(id);
        String matricule = donnees.getMatriculeVehicule() == null
            ? ""
            : donnees.getMatriculeVehicule().trim();
        if (matricule.isEmpty()) {
            throw new IllegalArgumentException("Le matricule du véhicule est obligatoire.");
        }
        livreur.setMatriculeVehicule(matricule);
        livreurRepository.save(livreur);
    }

    /**
     * Supprime le livreur seulement s'il existe afin qu'un ancien lien ne provoque pas une erreur.
     */
    public void supprimer(Integer id) {
        if (distributionRepository.existsByLivreur_Id(id)) {
            throw new IllegalArgumentException(
                "Ce livreur est déjà utilisé par une distribution et ne peut pas être supprimé."
            );
        }
        if (livreurRepository.existsById(id)) {
            livreurRepository.deleteById(id);
        }
    }

    /**
     * Produit la forme utilisée uniquement par la recherche textuelle interne au service.
     */
    private String normaliser(String valeur) {
        return valeur == null ? "" : valeur.trim().toLowerCase();
    }
}
