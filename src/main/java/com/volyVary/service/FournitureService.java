package com.volyVary.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.volyVary.model.Fourniture;
import com.volyVary.repository.FournitureRepository;

@Service
public class FournitureService {

    private final FournitureRepository fournitureRepository;

    /**
     * Injecte le repository des fournitures afin de centraliser les règles de consultation,
     * de création, de modification et de suppression dans une couche métier unique.
     */
    public FournitureService(FournitureRepository fournitureRepository) {
        this.fournitureRepository = fournitureRepository;
    }

    /**
     * Recherche les fournitures par nom ou fournisseur et applique éventuellement un filtre
     * de catégorie. Le résultat est trié par nom avant d'être transmis à la vue JSP.
     */
    public List<Fourniture> lister(String recherche, String categorie) {
        String texteRecherche = normaliser(recherche);
        String categorieRecherche = normaliser(categorie);

        return fournitureRepository.findAll().stream()
            .filter(fourniture -> texteRecherche.isEmpty()
                || normaliser(fourniture.getNom()).contains(texteRecherche)
                || normaliser(fourniture.getFournisseur()).contains(texteRecherche))
            .filter(fourniture -> categorieRecherche.isEmpty()
                || normaliser(fourniture.getCategorie()).equals(categorieRecherche))
            .sorted(Comparator.comparing(Fourniture::getNom, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    /**
     * Retourne la fourniture correspondant à l'identifiant reçu afin de préparer le formulaire
     * de modification, ou signale clairement que l'enregistrement demandé n'existe pas.
     */
    public Fourniture obtenir(Integer id) {
        return fournitureRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Fourniture introuvable : " + id));
    }

    /**
     * Enregistre une nouvelle fourniture ou applique les valeurs du formulaire à une fourniture
     * existante. La copie explicite des champs garde le traitement simple et prévisible.
     */
    public void enregistrer(Integer id, Fourniture donnees) {
        Fourniture fourniture = id == null ? new Fourniture() : obtenir(id);
        fourniture.setNom(donnees.getNom());
        fourniture.setCategorie(donnees.getCategorie());
        fourniture.setQuantite(donnees.getQuantite());
        fourniture.setPrix(donnees.getPrix());
        fourniture.setDate(donnees.getDate());
        fourniture.setFournisseur(donnees.getFournisseur());
        fournitureRepository.save(fourniture);
    }

    /**
     * Retire une fourniture de la base lorsqu'elle existe. Le contrôle préalable permet de
     * supporter sans erreur une demande répétée provenant du navigateur.
     */
    public void supprimer(Integer id) {
        if (fournitureRepository.existsById(id)) {
            fournitureRepository.deleteById(id);
        }
    }

    /**
     * Prépare une chaîne pour les recherches en supprimant les espaces externes, en ignorant
     * la casse et en remplaçant les valeurs nulles par une chaîne vide.
     */
    private String normaliser(String valeur) {
        return valeur == null ? "" : valeur.trim().toLowerCase();
    }
}
