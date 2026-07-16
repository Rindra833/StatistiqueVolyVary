package com.volyVary.service;

import com.volyVary.modele.*;
import com.volyVary.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CollecteService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CollecteRepository collecteRepository;

    @Autowired
    private LotPaddyRepository lotPaddyRepository;

    @Autowired
    private ReductionRepository reductionRepository;

    @Autowired
    private StatutLotPaddyRepository statutRepository;

    @Autowired
    private HistoriqueCollecteRepository historiqueRepository;

    

    public LotPaddy calculerCollecte(int clientId, Double quantite, Double prixUnitaire, Double tauxHumidite, String dateHeure) {
        validerDonneesCollecte(quantite, prixUnitaire, tauxHumidite);

       Client client = clientRepository.TrouverParIdClient(clientId);
        if (client == null) {
            return null;
        }

        Reduction reduction = reductionRepository.trouverReductionParTaux(tauxHumidite);

        double tauxReduction = 0;
        
        if (reduction != null) {
            tauxReduction = reduction.getReduction();
        }

        double prixCorrige = prixUnitaire * (1 - tauxReduction / 100);
        double prixTotal = quantite * prixCorrige;

        Collecte collecte = new Collecte();
        collecte.setPrixUnitaire(prixCorrige);

        LotPaddy lot = new LotPaddy();
        lot.setQuantite(quantite);
        lot.setTauxHumidite(tauxHumidite);
        lot.setDate(parseDate(dateHeure));
        lot.setPrixCollecte(prixTotal);
        lot.setCollecte(collecte);

        return lot;
    }



    @Transactional
    public LotPaddy enregistrerCollecte(int clientId, Double quantite, Double prixUnitaire, Double tauxHumidite, String dateHeure) {
        /*
         * La validation est répétée ici car ce service constitue la dernière frontière avant la
         * base. Ainsi, un appel direct ne peut pas insérer une collecte invalide ou partielle.
         */
        validerDonneesCollecte(quantite, prixUnitaire, tauxHumidite);
        Client client = clientRepository.TrouverParIdClient(clientId);
        if (client == null) {
            throw new IllegalArgumentException("Client introuvable : " + clientId);
        }

        /*
         * Le statut est obtenu avant de créer la collecte et le lot. L'ancienne version retournait
         * null après les insertions si la table de référence était vide, ce qui laissait un lot
         * orphelin. Cette méthode idempotente rend le module autonome dans une base nouvellement créée.
         */
        StatutLotPaddy statut = obtenirOuCreerStatut(
            "EN_ATTENTE",
            "En attente de paiement"
        );

        Reduction reduction = reductionRepository.trouverReductionParTaux(tauxHumidite);
        double tauxReduction = 0;
        if (reduction != null) {
            tauxReduction = reduction.getReduction();
        }

        double prixCorrige = prixUnitaire * (1 - tauxReduction / 100);
        double prixTotal = quantite * prixCorrige;

        Collecte collecte = new Collecte();
        collecte.setPrixUnitaire(prixCorrige);
        collecte = collecteRepository.save(collecte);

        LotPaddy lot = new LotPaddy();
        lot.setReference("LP" + System.currentTimeMillis());
        lot.setQuantite(quantite);
        lot.setTauxHumidite(tauxHumidite);
        lot.setDate(LocalDateTime.parse(dateHeure).toLocalDate());
        lot.setPrixCollecte(prixTotal);
        lot.setCollecte(collecte);
        lot = lotPaddyRepository.save(lot);

        HistoriqueCollecte historique = new HistoriqueCollecte();
        historique.setClient(client);
        historique.setLotPaddy(lot);
        historique.setStatut(statut);
        historique.setDateHistoriqueCollecte(parseDate(dateHeure));
        historiqueRepository.save(historique);

        return lot;
    }


    @Transactional
    public void payerLot(int idLot) {
        LotPaddy lot = lotPaddyRepository.trouverParIdLotPaddy(idLot);
        if (lot == null) {
            return;
        }

        StatutLotPaddy statutPaye = obtenirOuCreerStatut("VALIDE", "Validé");

        List<HistoriqueCollecte> historiques = historiqueRepository.trouverDernierHistoriqueParLot(idLot);
        Client client = null;
        if (!historiques.isEmpty()) {
            client = historiques.get(0).getClient();
        }

        HistoriqueCollecte historique = new HistoriqueCollecte();
        historique.setClient(client);
        historique.setLotPaddy(lot);
        historique.setStatut(statutPaye);
        historique.setDateHistoriqueCollecte(LocalDate.now());
        historiqueRepository.save(historique);
    }


    public List<LotPaddy> listerLots() {
        return lotPaddyRepository.trouverToutDateDec();
    }


    public LotPaddy obtenirLot(int id) {
        return lotPaddyRepository.trouverParIdLotPaddy(id);
    }


    public Double obtenirQuantiteTotale() {
        return lotPaddyRepository.obtenirQuantiteTotal();
    }

    
    public Double obtenirRecetteTotale() {
        return lotPaddyRepository.obtenirRecetteTotal();
    }

    public List<LotPaddy> listerLotsActif(){
        return lotPaddyRepository.trouverLotsActif();
    }

    public void annulerAchat(int idLot){   
        LotPaddy lot = lotPaddyRepository.trouverParIdLotPaddy(idLot);
        if (lot == null) {
            return;
        }

        StatutLotPaddy statutAnnule = obtenirOuCreerStatut("ANNULE", "Annulé");
        
        List<HistoriqueCollecte> historiques = historiqueRepository.trouverDernierHistoriqueParLot(idLot);
        Client client = null;
        if(!historiques.isEmpty()){
            client = historiques.get(0).getClient();
        } 

        HistoriqueCollecte historique = new HistoriqueCollecte();
        historique.setClient(client);
        historique.setLotPaddy(lot);
        historique.setStatut(statutAnnule);
        historique.setDateHistoriqueCollecte(LocalDate.now());
        historiqueRepository.save(historique);
    }


    private LocalDate parseDate(String dateHeure) {
        try {
            return LocalDateTime.parse(dateHeure).toLocalDate();
        } catch (Exception e) {
            throw new IllegalArgumentException("Format de date invalide");
        }
    }


    private void validerDonneesCollecte(Double quantite, Double prixUnitaire, Double tauxHumidite) {
        if (quantite == null) {
            throw new IllegalArgumentException("La quantité est obligatoire");
        }
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive  : " + quantite + " kg");
        }

        if (prixUnitaire == null) {
            throw new IllegalArgumentException("Le prix unitaire est obligatoire");
        }
        if (prixUnitaire <= 0) {
            throw new IllegalArgumentException("Le prix unitaire doit être positif  : " + prixUnitaire + " Ar");
        }

        if (tauxHumidite == null) {
            throw new IllegalArgumentException("Le taux d'humidité est obligatoire");
        }
        if (tauxHumidite < 0 || tauxHumidite > 100) {
            throw new IllegalArgumentException("Le taux d'humidité doit être entre 0 et 100%  : " + tauxHumidite + "%");
        }
    }

    /**
     * Retourne le statut fonctionnel demandé et le crée seulement s'il manque. Ces trois statuts
     * sont des constantes techniques du workflow de collecte, pas des données saisies par l'utilisateur.
     */
    private StatutLotPaddy obtenirOuCreerStatut(String sigle, String libelle) {
        StatutLotPaddy statut = statutRepository.trouverParSigle(sigle);
        if (statut != null) {
            return statut;
        }
        StatutLotPaddy nouveauStatut = new StatutLotPaddy();
        nouveauStatut.setSigle(sigle);
        nouveauStatut.setLibelle(libelle);
        return statutRepository.save(nouveauStatut);
    }
}
