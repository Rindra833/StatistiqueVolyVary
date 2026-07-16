package com.volyVary.service;

import com.volyVary.modele.*;
import com.volyVary.dto.*;
import com.volyVary.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DistributionService {

    @Autowired private DistributionRepository distributionRepo;
    @Autowired private DetailDistributionRepository detailRepo;
    @Autowired private LieuRepository lieuRepo;
    @Autowired private LivreurRepository livreurRepo;
    @Autowired private HistoriqueDistributionService historiqueDistributionService;
    @Autowired private ClientService clientService;
    @Autowired private DetailDistributionService detailDistributionService;

    @Transactional(readOnly = true)
    public List<Produit> chargerProduits() {
        return clientService.chargerProduits();
    }

    @Transactional(readOnly = true)
    public List<Lieu> chargerLieux() {
        return lieuRepo.findAll();
    }

    @Transactional(readOnly = true)
    public List<Livreur> chargerLivreurs() {
        return livreurRepo.findAll();
    }

    @Transactional
    public Distribution validerCommande(Client client, int idLieu, int idLivreur,
                                        List<LigneCommande> lignes) {
        Client clientSauvegarde = clientService.sauvegarderClient(client);
        Distribution distribution = sauvegarderDistribution(clientSauvegarde, idLieu, idLivreur);
        detailDistributionService.sauvegarderDetails(distribution, lignes);
        historiqueDistributionService.enregistrerStatut(distribution, "EN_COURS");
        return distribution;
    }

    @Transactional(readOnly = true)
    public Distribution chargerFacture(int idDistrib) {
        return distributionRepo.findById(idDistrib).orElseThrow(() -> new IllegalArgumentException("Distribution introuvable"));
    }

    @Transactional(readOnly = true)
    public List<DetailDistribution> chargerDetails(int idDistrib) {
        return detailRepo.findByDistributionId(idDistrib);
    }

    @Transactional(readOnly = true)
    public double calculerTotalFacture(int idDistrib) {
        return chargerDetails(idDistrib).stream()
                .mapToDouble(detail -> detail.getQuantite() * detail.getProduit().getPrixUnitaire())
                .sum();
    }

    @Transactional
    public void terminerCommande(int idDistrib) {
        historiqueDistributionService.enregistrerStatut(chargerFacture(idDistrib), "TERMINE");
    }

    @Transactional
    public void annulerCommande(int idDistrib) {
        historiqueDistributionService.enregistrerStatut(chargerFacture(idDistrib), "ANNULE");
    }

    @Transactional(readOnly = true)
    public String obtenirStatutActuel(int idDistrib) {
        return historiqueDistributionService.obtenirStatutActuel(chargerFacture(idDistrib));
    }

    @Transactional(readOnly = true)
    public List<Distribution> listerDistributions() {
        return distributionRepo.findAllByOrderByDateDesc();
    }

    @Transactional(readOnly = true)
    public Page<FactureResume> rechercherFactures(String recherche, String statut,
                                                    String tri, String direction,
                                                    int page, int taille) {
        List<FactureResume> filtres = facturesFiltrees(recherche, statut, tri, direction);

        int debut = page * taille;
        int fin = Math.min(debut + taille, filtres.size());
        List<FactureResume> facturesEnPage = debut < filtres.size() ? filtres.subList(debut, fin) : List.of();

        return new PageImpl<>(facturesEnPage, PageRequest.of(page, taille), filtres.size());
    }

    @Transactional(readOnly = true)
    public List<FactureResume> facturesFiltrees(String recherche, String statut, String tri, String direction) {
        List<FactureResume> toutes = listerFacturesResume();

        String rechercheNormalisee = recherche == null ? "" : recherche.trim().toLowerCase();
        List<FactureResume> filtrees = new ArrayList<>();
        for (FactureResume f : toutes) {
            boolean matchRecherche = rechercheNormalisee.isEmpty()
                    || f.getReference().toLowerCase().contains(rechercheNormalisee)
                    || f.getClientReference().toLowerCase().contains(rechercheNormalisee)
                    || f.getClientNom().toLowerCase().contains(rechercheNormalisee)
                    || f.getClientPrenom().toLowerCase().contains(rechercheNormalisee);
            boolean matchStatut = statut == null || statut.isBlank() || statut.equals(f.getStatut());
            if (matchRecherche && matchStatut) {
                filtrees.add(f);
            }
        }

        Comparator<FactureResume> comparateur = switch (tri == null ? "" : tri) {
            case "quantite" -> Comparator.comparingDouble(FactureResume::getQuantite);
            case "montant" -> Comparator.comparingDouble(FactureResume::getMontant);
            case "statut" -> Comparator.comparing(FactureResume::getStatut, String.CASE_INSENSITIVE_ORDER);
            case "clientReference" -> Comparator.comparing(FactureResume::getClientReference, String.CASE_INSENSITIVE_ORDER);
            case "date" -> Comparator.comparing(FactureResume::getDate);
            default -> Comparator.comparing(FactureResume::getReference, String.CASE_INSENSITIVE_ORDER);
        };
        if ("desc".equalsIgnoreCase(direction)) {
            comparateur = comparateur.reversed();
        }
        filtrees.sort(comparateur);
        return filtrees;
    }

    @Transactional(readOnly = true)
    public List<FactureResume> listerFacturesResume() {
        List<Distribution> distributions = distributionRepo.findAllByOrderByDateDesc();
        List<FactureResume> resumes = new ArrayList<>();
        for (Distribution distribution : distributions) {
            List<DetailDistribution> details = detailRepo.findByDistributionId(distribution.getId());
            double quantite = details.stream().mapToDouble(DetailDistribution::getQuantite).sum();
            double montant = details.stream()
                    .mapToDouble(d -> d.getQuantite() * d.getProduit().getPrixUnitaire())
                    .sum();
            String statut = historiqueDistributionService.obtenirStatutActuel(distribution);
            resumes.add(new FactureResume(distribution, quantite, montant, statut));
        }
        return resumes;
    }

    @Transactional(readOnly = true)
    public double calculerQteGlobale() {
        return detailRepo.sommeQteTotale();
    }

    @Transactional(readOnly = true)
    public double calculerRecetteTotale() {
        return detailRepo.sommeRecetteTotale();
    }

    @Transactional(readOnly = true)
    public double calculerQteParDistrib(int idDistrib) {
        return chargerDetails(idDistrib).stream().mapToDouble(DetailDistribution::getQuantite).sum();
    }

    private Distribution sauvegarderDistribution(Client client, int idLieu, int idLivreur) {
        Distribution distribution = new Distribution();
        distribution.setClient(client);
        distribution.setReference(genererRef());
        distribution.setLieu(lieuRepo.findById(idLieu).orElseThrow());
        distribution.setLivreur(livreurRepo.findById(idLivreur).orElseThrow());
        distribution.setDate(LocalDate.now());
        return distributionRepo.save(distribution);
    }

    private String genererRef() {
        long num = distributionRepo.count() + 1;
        return String.format("DIST%04d", num);
    }
}