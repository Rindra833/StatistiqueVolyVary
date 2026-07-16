package com.volyVary.controller;

import com.volyVary.modele.*;
import com.volyVary.service.*;
import com.volyVary.repository.*;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import java.util.List;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/collectes")
public class CollecteController {

    @Autowired
    private CollecteService collecteService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientService clientService;



    @GetMapping("/nouveau")
    public String formulaireNouveauAchat(Model model) {
        model.addAttribute("now", LocalDateTime.now());
        List<Client> clients = clientRepository.findAll();
        model.addAttribute("clients", clients);
        model.addAttribute("derniereReference", clientService.obtenirDerniereReference());
        return "collecte/formulaire-collecte";
    }


    @PostMapping("/calculer")
    public String calculerCollecte(@RequestParam String reference, @RequestParam String nom, @RequestParam String prenom, @RequestParam String telephone, @RequestParam String dateHeure, @RequestParam Double quantite, @RequestParam Double prixUnitaire, @RequestParam Double tauxHumidite, Model model, RedirectAttributes redirectAttributes) {
        try { 
        Client client = clientService.trouverOuCreerClient(reference, nom, prenom, telephone, dateHeure);
        
        if (client == null) {
            throw new IllegalArgumentException("Erreur lors de la création ou de la récupération du client");
        }

        LotPaddy lot = collecteService.calculerCollecte(client.getId(), quantite, prixUnitaire, tauxHumidite, dateHeure);

        model.addAttribute("dateHeure", dateHeure);
        model.addAttribute("lot", lot);
        model.addAttribute("client", client);
        model.addAttribute("prixUnitaireOriginal", prixUnitaire);
        
        return "collecte/validation-collecte";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/collectes/nouveau";
        }
    }


    @PostMapping("/enregistrer")
    public String enregistrerCollecte(@RequestParam int clientId, @RequestParam Double quantite, @RequestParam Double prixUnitaire, @RequestParam Double tauxHumidite, @RequestParam String dateHeure, RedirectAttributes redirectAttributes) {
        try { 
        LotPaddy lot = collecteService.enregistrerCollecte(clientId, quantite, prixUnitaire, tauxHumidite, dateHeure);
        
        if (lot != null) {
            return "redirect:/collectes/detail/" + lot.getIdLotPaddy();
        }   

        throw new IllegalArgumentException("Erreur lors de l'enregistrement de la collecte");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/collectes/nouveau";
        }
    }
}