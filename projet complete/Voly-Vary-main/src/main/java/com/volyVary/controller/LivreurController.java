package com.volyVary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.volyVary.modele.Livreur;
import com.volyVary.service.LivreurService;

@Controller
@RequestMapping
public class LivreurController {

    @Autowired
    private LivreurService livreurService;

    /**
     * Affiche les livreurs enregistrés et applique, si elle est renseignée, une recherche sur le
     * matricule du véhicule. Le contrôleur prépare uniquement les données nécessaires à la JSP.
     */
    @GetMapping("/admin/livreurs")
    public String lister(@RequestParam(defaultValue = "") String recherche, Model modele) {
        modele.addAttribute("livreurs", livreurService.lister(recherche));
        modele.addAttribute("recherche", recherche);
        modele.addAttribute("pageActive", "livreurs");
        return "admin/livreurs/liste";
    }

    /**
     * Prépare un objet vide pour afficher le formulaire de création. Aucun enregistrement n'est
     * effectué pendant cette requête GET.
     */
    @GetMapping("/admin/livreurs/nouveau")
    public String nouveau(Model modele) {
        preparerFormulaire(modele, new Livreur(), "Ajouter un livreur");
        return "admin/livreurs/formulaire";
    }

    /**
     * Charge le livreur demandé avant d'afficher le formulaire partagé avec la création.
     */
    @GetMapping("/admin/livreurs/{id}/modifier")
    public String modifier(@PathVariable Integer id, Model modele) {
        preparerFormulaire(modele, livreurService.obtenir(id), "Modifier le livreur");
        return "admin/livreurs/formulaire";
    }

    /**
     * Délègue la création ou la modification au service, puis applique le cycle POST → redirection
     * → GET afin d'éviter un double enregistrement lorsque la page est rafraîchie.
     */
    @PostMapping("/admin/livreurs/enregistrer")
    public String enregistrer(
        @RequestParam(required = false) Integer id,
        @ModelAttribute Livreur livreur,
        RedirectAttributes redirection
    ) {
        livreurService.enregistrer(id, livreur);
        redirection.addFlashAttribute("messageSucces", "Le livreur a été enregistré.");
        return "redirect:/admin/livreurs";
    }

    /**
     * Supprime le livreur indiqué dans l'URL puis revient à la liste avec un message temporaire.
     */
    @PostMapping("/admin/livreurs/{id}/supprimer")
    public String supprimer(@PathVariable Integer id, RedirectAttributes redirection) {
        try {
            livreurService.supprimer(id);
            redirection.addFlashAttribute("messageSucces", "Le livreur a été supprimé.");
        } catch (IllegalArgumentException exception) {
            redirection.addFlashAttribute("messageErreur", exception.getMessage());
        }
        return "redirect:/admin/livreurs";
    }

    /**
     * Centralise les attributs communs aux écrans de création et de modification.
     */
    private void preparerFormulaire(Model modele, Livreur livreur, String titre) {
        modele.addAttribute("livreur", livreur);
        modele.addAttribute("titre", titre);
        modele.addAttribute("pageActive", "livreurs");
    }
}
