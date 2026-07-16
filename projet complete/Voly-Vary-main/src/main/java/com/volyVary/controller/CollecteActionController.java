package com.volyVary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.volyVary.modele.*;
import com.volyVary.service.*;
import com.volyVary.repository.*;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import com.volyVary.modele.Client;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/collectes")
public class CollecteActionController {

    @Autowired
    private CollecteService collecteService;

    @Autowired
    private HistoriqueCollecteRepository historiqueCollecteRepository;

    @PostMapping("/payer-lot")
    public String payerLot(@RequestParam int idLot) {
        collecteService.payerLot(idLot);
        return "redirect:/collectes/detail/" + idLot;
    }


    @PostMapping("/annuler")
    public String annulerAchat(@RequestParam int idLot) {
        collecteService.annulerAchat(idLot);
        return "redirect:/collectes/valides";
    }


    @GetMapping("/detail/{id}")
    public String detailLot(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try { 
        LotPaddy lot = collecteService.obtenirLot(id);
        
        if (lot == null) {
            throw new IllegalArgumentException("Lot non trouvé");
        }
        
        List<HistoriqueCollecte> historique = historiqueCollecteRepository.trouverDernierHistoriqueParLot(id);

        
        Client client = null;
        String statutActuel = null;
        
        if (!historique.isEmpty()) {
            HistoriqueCollecte dernierHistorique = historique.get(0);
            client = dernierHistorique.getClient();
            statutActuel = dernierHistorique.getStatut().getSigle();
        }

        model.addAttribute("lot", lot);
        model.addAttribute("client", client);
        model.addAttribute("statutActuel", statutActuel);
        
        return "collecte/validation-collecte";
        } 
        catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "collecte/valides";
        }
    }


    @GetMapping("/imprimer/{id}")
    public String imprimer(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        LotPaddy lot = collecteService.obtenirLot(id);

        try { 
        if (lot == null) {
            throw new IllegalArgumentException("Lot non trouvé");
        }

        List<HistoriqueCollecte> historique = historiqueCollecteRepository.trouverDernierHistoriqueParLot(id);
        HistoriqueCollecte historiques = null;
        if (!historique.isEmpty()) {
            historiques = historique.get(0);
        }
        Client client = null;
        if(historiques != null) {
            client = historiques.getClient();
        }

        model.addAttribute("lot", lot);
        model.addAttribute("client", client);

        return "collecte/imprimer";
        }
        catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "collecte/valides";
        }
    }
}