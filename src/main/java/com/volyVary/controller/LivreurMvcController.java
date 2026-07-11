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

import com.volyVary.model.Livreur;
import com.volyVary.service.LivreurService;

@Controller
@RequestMapping
public class LivreurMvcController {

    private final LivreurService livreurService;

    /**
     * Injecte le service métier afin que le contrôleur se limite à recevoir les paramètres HTTP,
     * préparer le Model de la JSP et choisir la prochaine vue à afficher.
     */
    public LivreurMvcController(LivreurService livreurService) {
        this.livreurService = livreurService;
    }

    /**
     * Prépare la liste des livreurs et les critères de recherche pour un rendu entièrement côté
     * serveur. L'ancien chemin HTML est accepté afin de préserver les liens du template existant.
     */
    @GetMapping({"/admin/livreurs", "/pages/admin/livreurs/index.html"})
    public String lister(
        @RequestParam(defaultValue = "") String recherche,
        @RequestParam(defaultValue = "") String vehicule,
        @RequestParam(defaultValue = "") String disponibilite,
        Model modele
    ) {
        modele.addAttribute("livreurs", livreurService.lister(recherche, vehicule, disponibilite));
        modele.addAttribute("recherche", recherche);
        modele.addAttribute("vehiculeSelectionne", vehicule);
        modele.addAttribute("disponibiliteSelectionnee", disponibilite);
        modele.addAttribute("pageActive", "livreurs");
        return "livreurs/liste";
    }

    /**
     * Ouvre un formulaire vide pour la création d'un livreur. La même JSP est réutilisée pour
     * l'ajout et la modification afin de réduire le nombre de fichiers et de traitements.
     */
    @GetMapping("/admin/livreurs/nouveau")
    public String nouveau(Model modele) {
        preparerFormulaire(modele, new Livreur(), "Ajouter un livreur");
        return "livreurs/formulaire";
    }

    /**
     * Charge le livreur sélectionné, place ses valeurs dans le Model et affiche le formulaire
     * prérempli afin que l'utilisateur puisse effectuer sa modification.
     */
    @GetMapping("/admin/livreurs/{id}/modifier")
    public String modifier(@PathVariable Integer id, Model modele) {
        preparerFormulaire(modele, livreurService.obtenir(id), "Modifier le livreur");
        return "livreurs/formulaire";
    }

    /**
     * Reçoit le formulaire HTML, confie l'enregistrement au service puis applique le modèle
     * POST-Redirect-GET pour éviter qu'un rafraîchissement répète l'opération en base.
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
     * Supprime le livreur identifié par le formulaire POST, ajoute un message temporaire puis
     * redirige vers la liste afin de respecter le même flux POST-Redirect-GET.
     */
    @PostMapping("/admin/livreurs/{id}/supprimer")
    public String supprimer(@PathVariable Integer id, RedirectAttributes redirection) {
        livreurService.supprimer(id);
        redirection.addFlashAttribute("messageSucces", "Le livreur a été supprimé.");
        return "redirect:/admin/livreurs";
    }

    /**
     * Ajoute au Model les informations communes aux formulaires d'ajout et de modification.
     * Cette méthode privée évite de répéter les mêmes attributs dans deux actions différentes.
     */
    private void preparerFormulaire(Model modele, Livreur livreur, String titre) {
        modele.addAttribute("livreur", livreur);
        modele.addAttribute("titre", titre);
        modele.addAttribute("pageActive", "livreurs");
    }
}
