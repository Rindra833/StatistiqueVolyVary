package com.volyVary.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.volyVary.model.Livreur;
import com.volyVary.repository.LivreurRepository;

@Service
public class LivreurService {

    private final LivreurRepository livreurRepository;

    /**
     * Reçoit le repository par injection de dépendances afin que le service soit la seule
     * couche responsable de la lecture et de l'écriture des livreurs dans PostgreSQL.
     */
    public LivreurService(LivreurRepository livreurRepository) {
        this.livreurRepository = livreurRepository;
    }

    /**
     * Retourne les livreurs correspondant aux critères saisis dans le formulaire de recherche.
     * Les comparaisons ignorent la casse et les critères vides ne limitent pas les résultats.
     */
    public List<Livreur> lister(String recherche, String vehicule, String disponibilite) {
        String texteRecherche = normaliser(recherche);
        String vehiculeRecherche = normaliser(vehicule);
        String disponibiliteRecherche = normaliser(disponibilite);

        return livreurRepository.findAll().stream()
            .filter(livreur -> texteRecherche.isEmpty()
                || normaliser(livreur.getNom()).contains(texteRecherche)
                || normaliser(livreur.getTelephone()).contains(texteRecherche)
                || normaliser(livreur.getAdresse()).contains(texteRecherche))
            .filter(livreur -> vehiculeRecherche.isEmpty()
                || normaliser(livreur.getVehicule()).equals(vehiculeRecherche))
            .filter(livreur -> disponibiliteRecherche.isEmpty()
                || normaliser(livreur.getDisponibilite()).equals(disponibiliteRecherche))
            .sorted(Comparator.comparing(Livreur::getNom, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    /**
     * Charge un livreur existant pour préremplir le formulaire de modification. Une erreur
     * explicite est produite lorsque l'identifiant transmis ne correspond à aucune ligne.
     */
    public Livreur obtenir(Integer id) {
        return livreurRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Livreur introuvable : " + id));
    }

    /**
     * Crée un livreur lorsque l'identifiant est absent, ou met à jour la ligne existante dans
     * le cas contraire. Les champs autorisés sont copiés explicitement pour éviter toute
     * modification involontaire de l'identifiant persistant.
     */
    public void enregistrer(Integer id, Livreur donnees) {
        Livreur livreur = id == null ? new Livreur() : obtenir(id);
        livreur.setNom(donnees.getNom());
        livreur.setTelephone(donnees.getTelephone());
        livreur.setAdresse(donnees.getAdresse());
        livreur.setVehicule(donnees.getVehicule());
        livreur.setImmatriculation(donnees.getImmatriculation());
        livreur.setDisponibilite(donnees.getDisponibilite());
        livreurRepository.save(livreur);
    }

    /**
     * Supprime le livreur demandé uniquement lorsqu'il existe, ce qui rend l'action idempotente
     * et évite de présenter une erreur technique à l'utilisateur après un double clic.
     */
    public void supprimer(Integer id) {
        if (livreurRepository.existsById(id)) {
            livreurRepository.deleteById(id);
        }
    }

    /**
     * Transforme une valeur potentiellement nulle en texte minuscule et sans espaces externes.
     * Cette petite fonction évite de répéter les contrôles de nullité dans tous les filtres.
     */
    private String normaliser(String valeur) {
        return valeur == null ? "" : valeur.trim().toLowerCase();
    }
}
