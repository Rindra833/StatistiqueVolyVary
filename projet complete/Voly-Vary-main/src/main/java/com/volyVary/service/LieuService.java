package com.volyVary.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.volyVary.modele.Lieu;
import com.volyVary.repository.DistributionRepository;
import com.volyVary.repository.LieuRepository;

@Service
public class LieuService {

    private final LieuRepository lieuRepository;
    private final DistributionRepository distributionRepository;

    public LieuService(
        LieuRepository lieuRepository,
        DistributionRepository distributionRepository
    ) {
        this.lieuRepository = lieuRepository;
        this.distributionRepository = distributionRepository;
    }

    /**
     * Liste les lieux par ordre alphabétique et applique une recherche simple sur leur nom. Le lieu
     * reste volontairement minimal car la distribution n'utilise actuellement que son identifiant
     * et son libellé ; aucune structure géographique artificielle n'est créée.
     */
    @Transactional(readOnly = true)
    public List<Lieu> lister(String recherche) {
        String texte = normaliser(recherche);
        return lieuRepository.findAll().stream()
            .filter(lieu -> texte.isEmpty() || normaliser(lieu.getNom()).contains(texte))
            .sorted(Comparator.comparing(
                Lieu::getNom,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            ))
            .toList();
    }

    /**
     * Charge le lieu demandé pour le formulaire de modification ou signale un identifiant invalide.
     */
    @Transactional(readOnly = true)
    public Lieu obtenir(Integer id) {
        return lieuRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Lieu introuvable : " + id));
    }

    /**
     * Crée ou modifie un lieu après avoir retiré les espaces inutiles et vérifié que son nom est
     * renseigné. La commune reste inchangée si elle existe déjà dans une base enrichie par le groupe.
     */
    @Transactional
    public void enregistrer(Integer id, String nom) {
        String nomNettoye = nom == null ? "" : nom.trim();
        if (nomNettoye.isEmpty()) {
            throw new IllegalArgumentException("Le nom du lieu est obligatoire.");
        }
        Lieu lieu = id == null ? new Lieu() : obtenir(id);
        lieu.setNom(nomNettoye);
        lieuRepository.save(lieu);
    }

    /**
     * Empêche la suppression d'un lieu déjà présent sur une facture afin de préserver l'historique
     * des distributions. Un lieu encore inutilisé peut être supprimé normalement.
     */
    @Transactional
    public void supprimer(Integer id) {
        if (distributionRepository.existsByLieu_Id(id)) {
            throw new IllegalArgumentException(
                "Ce lieu est déjà utilisé par une distribution et ne peut pas être supprimé."
            );
        }
        if (lieuRepository.existsById(id)) {
            lieuRepository.deleteById(id);
        }
    }

    /** Normalise uniquement les valeurs utilisées par le filtre de recherche. */
    private String normaliser(String valeur) {
        return valeur == null ? "" : valeur.trim().toLowerCase();
    }
}
