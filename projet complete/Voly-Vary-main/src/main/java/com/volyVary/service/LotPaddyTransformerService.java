package com.volyVary.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.volyVary.dto.*;
import com.volyVary.modele.*;
import com.volyVary.repository.*;

@Service
public class LotPaddyTransformerService {
    @Autowired
    private TransformationRepository transformationRepository;

    @Autowired
    private LotPaddyMouvementRepository lotPaddyMouvementRepository;

    @Autowired
    private LotPaddyTransformeRepository lotPaddyTransformeRepository;

    @Autowired
    private LotPaddyRepository lotPaddyRepository;

    @Autowired
    private DetailLotPaddyTransformeService detailLotPaddyTransformeService;

    public List<LotStockDto> getlisteStockPaddy() {
        return lotPaddyMouvementRepository.getStockReelParLot();
    }

    /**
     * Transforme la quantité demandée avec le tarif saisi dans le formulaire. Toutes les écritures
     * sont réalisées dans une seule transaction : en cas de stock insuffisant, aucun lot, mouvement
     * ou tarif partiel ne reste enregistré dans la base.
     */
    @Transactional
    public LotPaddyTransforme transformation(
        Double quantiteSaisie,
        LocalDateTime date,
        Double prixUnitaire
    ) throws IllegalArgumentException {
        if (quantiteSaisie == null || quantiteSaisie <= 0) {
            throw new IllegalArgumentException("La quantité à transformer doit être positive.");
        }
        if (date == null) {
            throw new IllegalArgumentException("La date de transformation est obligatoire.");
        }
        if (prixUnitaire == null || prixUnitaire <= 0) {
            throw new IllegalArgumentException("Le prix unitaire de transformation doit être positif.");
        }

        Double somme = lotPaddyRepository.sommeQuantite();
        Double stockMouv = lotPaddyMouvementRepository.getStockPaddy();
        double total = (somme != null ? somme : 0.0) - (stockMouv != null ? stockMouv : 0.0);

        if (quantiteSaisie > total) {
            throw new IllegalArgumentException("Stock insuffisant pour la transformation demandée.");
        }

        memoriserTarif(prixUnitaire);

        Integer prochainNumero = lotPaddyTransformeRepository.getProchainNumeroReference();
        LotPaddyTransforme lotPaddyTransforme = new LotPaddyTransforme();
        lotPaddyTransforme.setDate(date);
        lotPaddyTransforme.setQuantite(quantiteSaisie);
        lotPaddyTransforme.setReference(String.format("LPT%04d", prochainNumero));
        lotPaddyTransforme.setPrixTransformation(prixUnitaire * quantiteSaisie);

        LotPaddyTransforme lotEnregistre = lotPaddyTransformeRepository.save(lotPaddyTransforme);

        List<LotStockDto> listeDto = lotPaddyMouvementRepository.getStockReelParLot();
        List<LotPaddyMouvement> mouvementsEnregistres = new ArrayList<>();
        double suivieQuantite = quantiteSaisie;

        for (LotStockDto l : listeDto) {
            if (suivieQuantite <= 0)
                break;

            LotPaddyMouvement m = new LotPaddyMouvement();
            m.setDate(date);
            m.setLotPaddyTransforme(lotEnregistre);

            LotPaddy lotPaddy = lotPaddyRepository.findById(l.getIdLot()).orElse(null);
            if (lotPaddy == null) {
                continue;
            }
            m.setLotPaddy(lotPaddy);

            double quantiteReel = l.getQuantiteReel();

            if (quantiteReel <= 0) {
                continue;
            }

            if (quantiteReel < suivieQuantite) {
                m.setQuantite(quantiteReel);
                suivieQuantite -= quantiteReel;
                mouvementsEnregistres.add(lotPaddyMouvementRepository.save(m));
            } else {
                m.setQuantite(suivieQuantite);
                mouvementsEnregistres.add(lotPaddyMouvementRepository.save(m));
                suivieQuantite = 0;
                break;
            }
        }
        detailLotPaddyTransformeService.insererDetailsProduits(
            lotEnregistre,
            mouvementsEnregistres
        );
        return lotEnregistre;
    }

    /**
     * Conserve le dernier tarif utilisé afin de le proposer automatiquement lors de la prochaine
     * transformation. Une nouvelle ligne n'est créée que lorsque le tarif change.
     */
    private void memoriserTarif(Double prixUnitaire) {
        Transformation tarifCourant = transformationRepository.findTopByOrderByIdTransformationDesc();
        if (tarifCourant == null || tarifCourant.getPrixUnitaire() == null
            || Double.compare(tarifCourant.getPrixUnitaire(), prixUnitaire) != 0) {
            Transformation nouveauTarif = new Transformation();
            nouveauTarif.setPrixUnitaire(prixUnitaire);
            transformationRepository.save(nouveauTarif);
        }
    }

    public Page<LotPaddyTransforme> filtrePaddyTransforme(
            LocalDateTime debut,
            LocalDateTime fin,
            Pageable pageable) {

        if (debut != null && fin != null) {
            return lotPaddyTransformeRepository
                    .findByDateBetween(debut, fin, pageable);
        }

        if (debut != null) {
            return lotPaddyTransformeRepository
                    .findByDateGreaterThanEqual(debut, pageable);
        }

        if (fin != null) {
            return lotPaddyTransformeRepository
                    .findByDateLessThanEqual(fin, pageable);
        }

        return lotPaddyTransformeRepository.findAll(pageable);
    }

    public int getLastLotTransformeId() {
        LotPaddyTransforme lastLotTransforme = lotPaddyTransformeRepository.findTopByOrderByIdDesc();
        if (lastLotTransforme != null) {
            return lastLotTransforme.getId();
        } else {
            return -1;
        }
    }

    public LotPaddyTransforme getLotPaddyTransformeById(int id) {
        return lotPaddyTransformeRepository.findById(id);
    }

    public List<LotPaddyMouvement> getLotPaddyTouche(int idLotTransforme) {
        return lotPaddyMouvementRepository.findByLotPaddyTransformeId(idLotTransforme);
    }

    public Page<LotPaddyTransforme> getAll(Pageable pageable) {
        return lotPaddyTransformeRepository.findAll(pageable);
    }

    public List<LotPaddyTransforme> listeHistorique() {
        return lotPaddyTransformeRepository.findAll();
    }

    public Double totalPaddyTransformer() {
        return lotPaddyTransformeRepository.quantiteTotalTransformer();
    }

    public void save(LotPaddyTransforme lotPaddyTransforme) {
        lotPaddyTransformeRepository.save(lotPaddyTransforme);
    }

    public Double totalPaddyTransformer(List<LotPaddyTransforme> liste) {
        double total = 0.0;
        for (LotPaddyTransforme lotPaddyTransforme : liste) {
            total += lotPaddyTransforme.getQuantite();
        }
        return total;
    }

    public Page<LotPaddyTransforme> search(String reference, Pageable pageable) {
        return lotPaddyTransformeRepository.rechercherParReference(reference, pageable);
    }
}
