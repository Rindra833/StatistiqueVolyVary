package com.volyVary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.volyVary.model.Fourniture;
import com.volyVary.service.FournitureService;

@Controller
@RequestMapping
public class FournitureMvcController {

    private final FournitureService fournitureService;

    /**
     * Fournit au contrôleur le service métier des fournitures. Le contrôleur reste ainsi consacré
     * à la navigation MVC tandis que la persistance demeure dans le service et le repository.
     */
    public FournitureMvcController(FournitureService fournitureService) {
        this.fournitureService = fournitureService;
    }

    /**
     * Exécute la recherche demandée, place les résultats et les filtres dans le Model puis retourne
     * la JSP de liste. L'ancien chemin HTML reste disponible comme alias de compatibilité.
     */
    @GetMapping({"/admin/fournitures", "/pages/admin/fournitures/index.html"})
    public String lister(
        @RequestParam(defaultValue = "") String recherche,
        @RequestParam(defaultValue = "") String categorie,
        Model modele
    ) {
        modele.addAttribute("fournitures", fournitureService.lister(recherche, categorie));
        modele.addAttribute("recherche", recherche);
        modele.addAttribute("categorieSelectionnee", categorie);
        modele.addAttribute("pageActive", "fournitures");
        return "fournitures/liste";
    }

    /**
     * Prépare une nouvelle fourniture et l'envoie à la JSP de formulaire pour permettre la saisie
     * côté navigateur sans générer le moindre champ HTML depuis JavaScript.
     */
    @GetMapping("/admin/fournitures/nouvelle")
    public String nouvelle(Model modele) {
        preparerFormulaire(modele, new Fourniture(), "Ajouter une fourniture");
        return "fournitures/formulaire";
    }

    /**
     * Charge la fourniture sélectionnée et affiche la même JSP avec les valeurs actuelles afin
     * de simplifier le code tout en conservant la fonctionnalité de modification.
     */
    @GetMapping("/admin/fournitures/{id}/modifier")
    public String modifier(@PathVariable Integer id, Model modele) {
        preparerFormulaire(modele, fournitureService.obtenir(id), "Modifier la fourniture");
        return "fournitures/formulaire";
    }

    /**
     * Traite les données du formulaire, délègue la sauvegarde au service et redirige vers la liste.
     * Cette redirection empêche la répétition accidentelle du POST lors d'un rafraîchissement.
     */
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

    /**
     * Supprime la fourniture envoyée par un formulaire POST protégé par CSRF, puis redirige vers
     * la liste avec un message de confirmation conservé pendant une seule requête.
     */
    @PostMapping("/admin/fournitures/{id}/supprimer")
    public String supprimer(@PathVariable Integer id, RedirectAttributes redirection) {
        fournitureService.supprimer(id);
        redirection.addFlashAttribute("messageSucces", "La fourniture a été supprimée.");
        return "redirect:/admin/fournitures";
    }

    /**
     * Centralise les attributs communs aux deux modes du formulaire pour garder les actions
     * publiques courtes et faciles à lire par un étudiant.
     */
    private void preparerFormulaire(Model modele, Fourniture fourniture, String titre) {
        modele.addAttribute("fourniture", fourniture);
        modele.addAttribute("titre", titre);
        modele.addAttribute("pageActive", "fournitures");
    }
}
