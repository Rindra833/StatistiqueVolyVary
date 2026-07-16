package com.volyVary.service;

import com.volyVary.modele.Distribution;
import com.volyVary.modele.HistoriqueDistribution;
import com.volyVary.modele.StatutDistribution;
import com.volyVary.repository.HistoriqueDistributionRepository;
import com.volyVary.repository.StatutDistributionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class HistoriqueDistributionService {

    @Autowired
    private HistoriqueDistributionRepository historiqueRepo;

    @Autowired
    private StatutDistributionRepository statutRepo;

    @Transactional
    public void enregistrerStatut(Distribution distribution, String sigle) {
        /*
         * Les statuts sont les étapes techniques fixes du workflow. Les créer à la première
         * utilisation permet à une base initialisée par Hibernate de traiter immédiatement une
         * commande, sans dépendre d'un script SQL externe oublié pendant l'intégration.
         */
        StatutDistribution statut = statutRepo.findBySigle(sigle)
                .orElseGet(() -> creerStatut(sigle));

        HistoriqueDistribution historique = new HistoriqueDistribution();
        historique.setDistribution(distribution);
        historique.setStatut(statut);
        historique.setDate(LocalDate.now());
        historiqueRepo.save(historique);
    }

    /**
     * Construit le libellé utilisateur correspondant au sigle interne demandé par le service.
     */
    private StatutDistribution creerStatut(String sigle) {
        StatutDistribution statut = new StatutDistribution();
        statut.setSigle(sigle);
        statut.setLibelle(switch (sigle) {
            case "EN_COURS" -> "En cours";
            case "TERMINE" -> "Terminé";
            case "ANNULE" -> "Annulé";
            default -> throw new IllegalArgumentException("Statut inconnu : " + sigle);
        });
        return statutRepo.save(statut);
    }

    @Transactional(readOnly = true)
    public String obtenirStatutActuel(Distribution distribution) {
        List<HistoriqueDistribution> historiques = historiqueRepo.findByDistributionIdOrderByDateAsc(distribution.getId());
        if (historiques.isEmpty()) {
            return "En cours";
        }
        return historiques.get(historiques.size() - 1).getStatut().getLibelle();
    }
}
