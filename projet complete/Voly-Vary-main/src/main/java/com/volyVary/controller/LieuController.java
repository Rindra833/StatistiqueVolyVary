package com.volyVary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.volyVary.modele.Lieu;
import com.volyVary.service.LieuService;

@Controller
public class LieuController {

    private final LieuService lieuService;

    public LieuController(LieuService lieuService) {
        this.lieuService = lieuService;
    }

    /** Affiche le référentiel des lieux utilisables dans le formulaire de distribution. */
    @GetMapping("/admin/lieux")
    public String lister(@RequestParam(defaultValue = "") String recherche, Model modele) {
        modele.addAttribute("lieux", lieuService.lister(recherche));
        modele.addAttribute("recherche", recherche);
        return "admin/lieux/liste";
    }

    /** Prépare le formulaire de création avec un lieu vide. */
    @GetMapping("/admin/lieux/nouveau")
    public String nouveau(Model modele) {
        preparerFormulaire(modele, new Lieu(), "Ajouter un lieu");
        return "admin/lieux/formulaire";
    }

    /** Charge le lieu existant et réutilise le formulaire de création pour sa modification. */
    @GetMapping("/admin/lieux/{id}/modifier")
    public String modifier(@PathVariable Integer id, Model modele) {
        preparerFormulaire(modele, lieuService.obtenir(id), "Modifier le lieu");
        return "admin/lieux/formulaire";
    }

    /**
     * Enregistre le formulaire puis redirige vers la liste selon le cycle POST → redirection → GET.
     */
    @PostMapping("/admin/lieux/enregistrer")
    public String enregistrer(
        @RequestParam(required = false) Integer id,
        @RequestParam String nom,
        RedirectAttributes redirection
    ) {
        try {
            lieuService.enregistrer(id, nom);
            redirection.addFlashAttribute("messageSucces", "Le lieu a été enregistré.");
            return "redirect:/admin/lieux";
        } catch (IllegalArgumentException exception) {
            redirection.addFlashAttribute("messageErreur", exception.getMessage());
            return id == null
                ? "redirect:/admin/lieux/nouveau"
                : "redirect:/admin/lieux/" + id + "/modifier";
        }
    }

    /** Supprime un lieu inutilisé et affiche une explication lorsqu'une facture le référence. */
    @PostMapping("/admin/lieux/{id}/supprimer")
    public String supprimer(@PathVariable Integer id, RedirectAttributes redirection) {
        try {
            lieuService.supprimer(id);
            redirection.addFlashAttribute("messageSucces", "Le lieu a été supprimé.");
        } catch (IllegalArgumentException exception) {
            redirection.addFlashAttribute("messageErreur", exception.getMessage());
        }
        return "redirect:/admin/lieux";
    }

    /** Centralise les attributs communs aux deux affichages du formulaire. */
    private void preparerFormulaire(Model modele, Lieu lieu, String titre) {
        modele.addAttribute("lieu", lieu);
        modele.addAttribute("titre", titre);
    }
}
