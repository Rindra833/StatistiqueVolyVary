package com.volyVary.controller;

import com.volyVary.modele.LotPaddy;
import com.volyVary.service.CollecteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CollectePartage {

    @Autowired
    private CollecteService collecteService;

    public List<LotPaddy> getLotsFiltresEtTries(String reference, String dateMin, String dateMax, Double quantiteMin, Double quantiteMax, Double prixMin, Double prixMax,
    Double totalMin, Double totalMax, String triePar, String ordre) {
        List<LotPaddy> lots = collecteService.listerLotsActif();
        lots = filtrerLots(lots, reference, dateMin, dateMax, quantiteMin, quantiteMax, prixMin, prixMax, totalMin, totalMax);
        lots = trierLots(lots, triePar, ordre);
        return lots;
    }


    public List<LotPaddy> filtrerLots(List<LotPaddy> lots, String reference, String dateMin, String dateMax, Double quantiteMin, Double quantiteMax, 
        Double prixMin, Double prixMax, Double totalMin, Double totalMax) {
        return lots.stream()
            .filter(lot -> {
                if (reference != null && !reference.trim().isEmpty()) {
                    if (!lot.getReference().toLowerCase().contains(reference.trim().toLowerCase())) {
                        return false;
                    }
                }

                if (dateMin != null && !dateMin.isEmpty()) {
                    LocalDate min = LocalDate.parse(dateMin);
                    if (lot.getDate().isBefore(min)) {
                        return false;
                    }
                }

                if (dateMax != null && !dateMax.isEmpty()) {
                    LocalDate max = LocalDate.parse(dateMax);
                    if (lot.getDate().isAfter(max)) {
                        return false;
                    }
                }

                if (quantiteMin != null && lot.getQuantite() < quantiteMin) {
                    return false;
                }

                if (quantiteMax != null && lot.getQuantite() > quantiteMax) {
                    return false;
                }

                if (prixMin != null && lot.getCollecte().getPrixUnitaire() < prixMin) {
                    return false;
                }

                if (prixMax != null && lot.getCollecte().getPrixUnitaire() > prixMax) {
                    return false;
                }

                if (totalMin != null && lot.getPrixCollecte() < totalMin) {
                    return false;
                }

                if (totalMax != null && lot.getPrixCollecte() > totalMax) {
                    return false;
                }

                return true;
            })
            .collect(Collectors.toList());
    }



    public List<LotPaddy> trierLots(List<LotPaddy> lots, String trierPar, String ordre) {
        if (trierPar == null || trierPar.isEmpty()) {
            return lots;
        }

        Comparator<LotPaddy> comparateur = null;

        switch (trierPar) {
            case "reference":
                comparateur = Comparator.comparing(lot -> lot.getReference());
                break;

            case "date":
                comparateur = Comparator.comparing(lot -> lot.getDate());
                break;

            case "quantite":
                comparateur = Comparator.comparing(lot -> lot.getQuantite());
                break;

            case "prix":
                comparateur = Comparator.comparing(lot -> lot.getCollecte().getPrixUnitaire());
                break;

            case "total":
                comparateur = Comparator.comparing(lot -> lot.getPrixCollecte());
                break;
        }

        if (comparateur == null) {
            return lots;
        }

        if ("desc".equalsIgnoreCase(ordre)) {
            comparateur = comparateur.reversed();
        }

        lots.sort(comparateur);

        return lots;
    }
}