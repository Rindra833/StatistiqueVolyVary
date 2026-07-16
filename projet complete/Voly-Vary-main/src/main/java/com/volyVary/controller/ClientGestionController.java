package com.volyVary.controller;

import java.time.LocalDate;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.volyVary.modele.Client;
import com.volyVary.service.ClientService;

@Controller
public class ClientGestionController {

    private final ClientService clientService;

    public ClientGestionController(ClientService clientService) {
        this.clientService = clientService;
    }

    /** Affiche le répertoire partagé et applique la recherche demandée par l'utilisateur. */
    @GetMapping("/clients")
    public String lister(
        @RequestParam(defaultValue = "") String recherche,
        Model modele
    ) {
        modele.addAttribute("clients", clientService.listerClients(recherche));
        modele.addAttribute("recherche", recherche);
        modele.addAttribute("pageActive", "clients");
        return "client/liste";
    }

    /** Prépare un formulaire avec une référence proposée, encore modifiable par l'utilisateur. */
    @GetMapping("/clients/nouveau")
    public String nouveau(Model modele) {
        Client client = new Client();
        client.setReference(clientService.obtenirDerniereReference());
        client.setDate(LocalDate.now());
        preparerFormulaire(modele, client, "Ajouter un client");
        return "client/formulaire";
    }

    /** Charge les données existantes dans le même formulaire afin d'éviter une vue dupliquée. */
    @GetMapping("/clients/{id}/modifier")
    public String modifier(@PathVariable Integer id, Model modele) {
        Client client = clientService.getClientParId(id);
        if (client == null) {
            throw new IllegalArgumentException("Client introuvable : " + id);
        }
        preparerFormulaire(modele, client, "Modifier le client");
        return "client/formulaire";
    }

    /** Enregistre le formulaire puis applique le modèle POST → redirection → GET. */
    @PostMapping("/clients/enregistrer")
    public String enregistrer(
        @RequestParam(required = false) Integer id,
        @ModelAttribute Client client,
        Model modele,
        RedirectAttributes redirection
    ) {
        try {
            clientService.enregistrerClient(id, client);
            redirection.addFlashAttribute("messageSucces", "Le client a été enregistré.");
            return "redirect:/clients";
        } catch (IllegalArgumentException exception) {
            modele.addAttribute("erreur", exception.getMessage());
            preparerFormulaire(
                modele,
                client,
                id == null ? "Ajouter un client" : "Modifier le client"
            );
            return "client/formulaire";
        }
    }

    /** Refuse proprement la suppression lorsqu'un historique métier utilise déjà le client. */
    @PostMapping("/clients/{id}/supprimer")
    public String supprimer(@PathVariable Integer id, RedirectAttributes redirection) {
        try {
            clientService.supprimerClient(id);
            redirection.addFlashAttribute("messageSucces", "Le client a été supprimé.");
        } catch (DataIntegrityViolationException exception) {
            redirection.addFlashAttribute(
                "erreur",
                "Ce client est utilisé par une opération et ne peut pas être supprimé."
            );
        }
        return "redirect:/clients";
    }

    /** Centralise les attributs communs aux formulaires d'ajout et de modification. */
    private void preparerFormulaire(Model modele, Client client, String titre) {
        modele.addAttribute("client", client);
        modele.addAttribute("titre", titre);
        modele.addAttribute("pageActive", "clients");
    }
}
