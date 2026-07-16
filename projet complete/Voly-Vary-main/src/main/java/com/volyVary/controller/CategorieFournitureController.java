package com.volyVary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.volyVary.modele.CategorieFourniture;
import com.volyVary.service.CategorieFournitureService;

@Controller
public class CategorieFournitureController {

    private final CategorieFournitureService categorieService;

    public CategorieFournitureController(CategorieFournitureService categorieService) {
        this.categorieService = categorieService;
    }

    /**
     * Affiche le référentiel des catégories et applique éventuellement une recherche sur le libellé.
     */
    @GetMapping("/admin/categories-fournitures")
    public String lister(@RequestParam(defaultValue = "") String recherche, Model modele) {
        modele.addAttribute("categories", categorieService.lister(recherche));
        modele.addAttribute("recherche", recherche);
        return "admin/categories-fournitures/liste";
    }

    /**
     * Prépare une catégorie vide pour le formulaire de création, sans écrire dans la base.
     */
    @GetMapping("/admin/categories-fournitures/nouvelle")
    public String nouvelle(Model modele) {
        preparerFormulaire(modele, new CategorieFourniture(), "Ajouter une catégorie");
        return "admin/categories-fournitures/formulaire";
    }

    /**
     * Charge la catégorie sélectionnée puis réutilise le formulaire commun pour sa modification.
     */
    @GetMapping("/admin/categories-fournitures/{id}/modifier")
    public String modifier(@PathVariable Integer id, Model modele) {
        preparerFormulaire(modele, categorieService.obtenir(id), "Modifier la catégorie");
        return "admin/categories-fournitures/formulaire";
    }

    /**
     * Reçoit le libellé, délègue les validations au service et applique le cycle POST → redirection
     * → GET. En cas d'erreur, le message est conservé pendant la redirection vers le formulaire.
     */
    @PostMapping("/admin/categories-fournitures/enregistrer")
    public String enregistrer(
        @RequestParam(required = false) Integer id,
        @RequestParam String libelle,
        RedirectAttributes redirection
    ) {
        try {
            categorieService.enregistrer(id, libelle);
            redirection.addFlashAttribute("messageSucces", "La catégorie a été enregistrée.");
            return "redirect:/admin/categories-fournitures";
        } catch (IllegalArgumentException exception) {
            redirection.addFlashAttribute("messageErreur", exception.getMessage());
            return id == null
                ? "redirect:/admin/categories-fournitures/nouvelle"
                : "redirect:/admin/categories-fournitures/" + id + "/modifier";
        }
    }

    /**
     * Supprime la catégorie demandée uniquement si le service confirme qu'aucune fourniture ne
     * l'utilise, puis affiche le résultat sur la liste.
     */
    @PostMapping("/admin/categories-fournitures/{id}/supprimer")
    public String supprimer(@PathVariable Integer id, RedirectAttributes redirection) {
        try {
            categorieService.supprimer(id);
            redirection.addFlashAttribute("messageSucces", "La catégorie a été supprimée.");
        } catch (IllegalArgumentException exception) {
            redirection.addFlashAttribute("messageErreur", exception.getMessage());
        }
        return "redirect:/admin/categories-fournitures";
    }

    /** Centralise les attributs communs aux écrans de création et de modification. */
    private void preparerFormulaire(
        Model modele,
        CategorieFourniture categorie,
        String titre
    ) {
        modele.addAttribute("categorie", categorie);
        modele.addAttribute("titre", titre);
    }
}
