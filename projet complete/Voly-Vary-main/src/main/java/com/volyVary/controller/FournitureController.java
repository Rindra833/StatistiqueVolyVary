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

import com.volyVary.modele.*;
import com.volyVary.service.*;

@Controller
@RequestMapping
public class FournitureController {

    @Autowired
    private FournitureService fournitureService;

    @Autowired
    private CategorieFournitureService categorieFournitureService;

    /**
     * Affiche les fournitures avec les catégories du projet complet. Le filtre catégorie reçoit
     * directement l'identifiant stocké dans fourniture.id_categorie.
     */
    @GetMapping("/admin/fournitures")
    public String lister(
        @RequestParam(defaultValue = "") String recherche,
        @RequestParam(required = false) Integer categorie,
        Model modele
    ) {
        modele.addAttribute("fournitures", fournitureService.lister(recherche, categorie));
        modele.addAttribute("categoriesFourniture", categorieFournitureService.getCategories());
        modele.addAttribute("recherche", recherche);
        modele.addAttribute("categorieSelectionnee", categorie);
        modele.addAttribute("pageActive", "fournitures");
        return "admin/fournitures/liste";
    }

    @GetMapping("/admin/fournitures/nouvelle")
    public String nouvelle(Model modele) {
        preparerFormulaire(modele, new Fourniture(), "Ajouter une fourniture");
        return "admin/fournitures/formulaire";
    }

    @GetMapping("/admin/fournitures/{id}/modifier")
    public String modifier(@PathVariable Integer id, Model modele) {
        preparerFormulaire(modele, fournitureService.obtenir(id), "Modifier la fourniture");
        return "admin/fournitures/formulaire";
    }

    @PostMapping("/admin/fournitures/enregistrer")
    public String enregistrer(
        @RequestParam(required = false) Integer id,
        @ModelAttribute Fourniture fourniture,
        RedirectAttributes redirection
    ) {
        fournitureService.enregistrer(id, fourniture);
        redirection.addFlashAttribute("messageSucces", "La fourniture a été enregistrée.");
        return "redirect:/admin/fournitures";
    }

    @PostMapping("/admin/fournitures/{id}/supprimer")
    public String supprimer(@PathVariable Integer id, RedirectAttributes redirection) {
        fournitureService.supprimer(id);
        redirection.addFlashAttribute("messageSucces", "La fourniture a été supprimée.");
        return "redirect:/admin/fournitures";
    }

    private void preparerFormulaire(Model modele, Fourniture fourniture, String titre) {
        modele.addAttribute("fourniture", fourniture);
        modele.addAttribute("categoriesFourniture", categorieFournitureService.getCategories());
        modele.addAttribute("titre", titre);
        modele.addAttribute("pageActive", "fournitures");
    }
}
