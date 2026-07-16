package com.volyVary.controller;

import com.volyVary.modele.HistoriqueCollecte;
import com.volyVary.modele.LotPaddy;
import com.volyVary.repository.HistoriqueCollecteRepository;
import com.volyVary.service.CollecteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/collectes")
public class CollecteListeController {

    @Autowired
    private CollecteService collecteService;

    @Autowired
    private HistoriqueCollecteRepository historiqueCollecteRepository;

    @Autowired
    private CollectePartage collectePartage;

    @GetMapping("/liste")
    public String listerLots(Model model, @RequestParam(required = false) String reference, @RequestParam(required = false) String dateMin, @RequestParam(required = false) String dateMax, @RequestParam(required = false) Double quantiteMin, @RequestParam(required = false) Double quantiteMax, @RequestParam(required = false) Double prixMin, @RequestParam(required = false) Double prixMax, @RequestParam(required = false) Double totalMin, @RequestParam(required = false) Double totalMax, @RequestParam(defaultValue = "0") int page,
    @RequestParam(required = false) String triePar, @RequestParam(required = false) String ordre) {
        List<LotPaddy> lots = collectePartage.getLotsFiltresEtTries(reference, dateMin, dateMax, quantiteMin, quantiteMax, prixMin, prixMax, totalMin, totalMax, triePar, ordre);

        int pageMax = 10;
        int startPage = page * pageMax;
        int endPage = Math.min(startPage + pageMax, lots.size());
        List<LotPaddy> lotsEnPage = startPage < lots.size() ? lots.subList(startPage, endPage) : List.of();
        Page<LotPaddy> pageLots = new PageImpl<>(lotsEnPage, PageRequest.of(page, pageMax), lots.size());

        Double quantiteTotale = collecteService.obtenirQuantiteTotale();
        Double recetteTotale = collecteService.obtenirRecetteTotale();

        model.addAttribute("lots", pageLots.getContent());
        model.addAttribute("pageCourante", pageLots.getNumber());
        model.addAttribute("pageTotales", pageLots.getTotalPages());
        model.addAttribute("quantiteTotale", quantiteTotale != null ? quantiteTotale : 0);
        model.addAttribute("recetteTotale", recetteTotale != null ? recetteTotale : 0);
        
        model.addAttribute("reference", reference);
        model.addAttribute("dateMin", dateMin);
        model.addAttribute("dateMax", dateMax);
        model.addAttribute("quantiteMin", quantiteMin);
        model.addAttribute("quantiteMax", quantiteMax);
        model.addAttribute("prixMin", prixMin);
        model.addAttribute("prixMax", prixMax);
        model.addAttribute("totalMin", totalMin);
        model.addAttribute("totalMax", totalMax);

        model.addAttribute("triePar", triePar);
        model.addAttribute("ordre", ordre);

        return "collecte/liste-lots";
    }


    
    @GetMapping("/en-attente")
    public String lotsEnAttente(Model model,@RequestParam(required = false) String reference,
    @RequestParam(required = false) String dateMin, @RequestParam(required = false) String dateMax,
    @RequestParam(required = false) Double quantiteMin,@RequestParam(required = false) Double quantiteMax,@RequestParam(required = false) Double prixMin,
    @RequestParam(required = false) Double prixMax, @RequestParam(required = false) Double totalMin, @RequestParam(required = false) Double totalMax,
    @RequestParam(required = false) String triePar, @RequestParam(required = false) String ordre, @RequestParam(defaultValue = "0") int page) {

        List<LotPaddy> tousLesLots = collecteService.listerLotsActif();
        List<LotPaddy> lotsEnAttente = new ArrayList<>();

        for (LotPaddy lot : tousLesLots) {
            List<HistoriqueCollecte> historiques = historiqueCollecteRepository.trouverDernierHistoriqueParLot(lot.getIdLotPaddy());
            if (!historiques.isEmpty() && "EN_ATTENTE".equals(historiques.get(0).getStatut().getSigle())) {
                lotsEnAttente.add(lot);
            }
        }

        lotsEnAttente = collectePartage.filtrerLots(lotsEnAttente, reference, dateMin, dateMax, quantiteMin, quantiteMax, prixMin, prixMax, totalMin, totalMax);
        lotsEnAttente = collectePartage.trierLots(lotsEnAttente, triePar, ordre);

        Double quantiteTotale = lotsEnAttente.stream().mapToDouble(LotPaddy::getQuantite).sum();
        Double recetteTotale = lotsEnAttente.stream().mapToDouble(LotPaddy::getPrixCollecte).sum();

        int pageMax = 10;
        int commencePage = page * pageMax;
        int finPage = Math.min(commencePage + pageMax, lotsEnAttente.size());
        List<LotPaddy> lotsEnPage = commencePage < lotsEnAttente.size() ? lotsEnAttente.subList(commencePage, finPage) : List.of();
        Page<LotPaddy> pageLots = new PageImpl<>(lotsEnPage, PageRequest.of(page, pageMax), lotsEnAttente.size());

        model.addAttribute("lots", pageLots.getContent());
        model.addAttribute("pageCourante", pageLots.getNumber());
        model.addAttribute("pageTotales", pageLots.getTotalPages());
        model.addAttribute("quantiteTotale", quantiteTotale);
        model.addAttribute("recetteTotale", recetteTotale);
        model.addAttribute("reference", reference);
        model.addAttribute("dateMin", dateMin);
        model.addAttribute("dateMax", dateMax);
        model.addAttribute("quantiteMin", quantiteMin);
        model.addAttribute("quantiteMax", quantiteMax);
        model.addAttribute("prixMin", prixMin);
        model.addAttribute("prixMax", prixMax);
        model.addAttribute("totalMin", totalMin);
        model.addAttribute("totalMax", totalMax);
        model.addAttribute("triePar", triePar);
        model.addAttribute("ordre", ordre);

        return "collecte/liste-en-attente";
    }




    @GetMapping("/valides")
    public String lotsValides(Model model, @RequestParam(required = false) String reference, @RequestParam(required = false) String dateMin,
    @RequestParam(required = false) String dateMax, @RequestParam(required = false) Double quantiteMin, @RequestParam(required = false) Double quantiteMax,
    @RequestParam(required = false) Double prixMin, @RequestParam(required = false) Double prixMax, @RequestParam(required = false) Double totalMin,
    @RequestParam(required = false) Double totalMax, @RequestParam(required = false) String triePar,
    @RequestParam(required = false) String ordre, @RequestParam(defaultValue = "0") int page) {

        List<LotPaddy> tousLesLots = collecteService.listerLotsActif();
        List<LotPaddy> lotsValides = new ArrayList<>();

        for (LotPaddy lot : tousLesLots) {
            List<HistoriqueCollecte> historiques = historiqueCollecteRepository.trouverDernierHistoriqueParLot(lot.getIdLotPaddy());
            if (!historiques.isEmpty() && "VALIDE".equals(historiques.get(0).getStatut().getSigle())) {
                lotsValides.add(lot);
            }
        }

        lotsValides = collectePartage.filtrerLots(lotsValides, reference, dateMin, dateMax, quantiteMin, quantiteMax, prixMin, prixMax, totalMin, totalMax);
        lotsValides = collectePartage.trierLots(lotsValides, triePar, ordre);

        Double quantiteTotale = lotsValides.stream().mapToDouble(LotPaddy::getQuantite).sum();
        Double recetteTotale = lotsValides.stream().mapToDouble(LotPaddy::getPrixCollecte).sum();

        int pageMax = 10;
        int commencePage = page * pageMax;
        int finPage = Math.min(commencePage + pageMax, lotsValides.size());
        List<LotPaddy> lotsEnPage = commencePage < lotsValides.size() ? lotsValides.subList(commencePage, finPage) : List.of();
        Page<LotPaddy> pageLots = new PageImpl<>(lotsEnPage, PageRequest.of(page, pageMax), lotsValides.size());

        model.addAttribute("lots", pageLots.getContent());
        model.addAttribute("pageCourante", pageLots.getNumber());
        model.addAttribute("pageTotales", pageLots.getTotalPages());
        model.addAttribute("quantiteTotale", quantiteTotale);
        model.addAttribute("recetteTotale", recetteTotale);
        model.addAttribute("reference", reference);
        model.addAttribute("dateMin", dateMin);
        model.addAttribute("dateMax", dateMax);
        model.addAttribute("quantiteMin", quantiteMin);
        model.addAttribute("quantiteMax", quantiteMax);
        model.addAttribute("prixMin", prixMin);
        model.addAttribute("prixMax", prixMax);
        model.addAttribute("totalMin", totalMin);
        model.addAttribute("totalMax", totalMax);
        model.addAttribute("triePar", triePar);
        model.addAttribute("ordre", ordre);

        return "collecte/liste-valides";
    }
}
