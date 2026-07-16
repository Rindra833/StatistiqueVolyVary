package com.volyVary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.volyVary.modele.*;
import com.volyVary.service.*;

@Controller
@RequestMapping
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

   
    @GetMapping({"/admin/utilisateurs", "/pages/admin/utilisateurs/index.html"})
    public String lister(
        @RequestParam(defaultValue = "") String recherche,
        @RequestParam(defaultValue = "") String role,
        Model modele
    ) {
        modele.addAttribute("utilisateurs", utilisateurService.lister(recherche, role));
        modele.addAttribute("roles", utilisateurService.listerRoles());
        modele.addAttribute("recherche", recherche);
        modele.addAttribute("roleSelectionne", role);
        modele.addAttribute("pageActive", "utilisateurs");
        return "admin/utilisateurs/liste";
    }

    @GetMapping("/admin/utilisateurs/nouveau")
    public String nouveau(Model modele) {
        preparerFormulaire(modele, new Utilisateur(), "Créer un compte");
        return "admin/utilisateurs/formulaire";
    }

    @GetMapping("/admin/utilisateurs/{id}/modifier")
    public String modifier(@PathVariable Integer id, Model modele) {
        preparerFormulaire(modele, utilisateurService.obtenir(id), "Modifier le compte");
        return "admin/utilisateurs/formulaire";
    }

    @PostMapping("/admin/utilisateurs/enregistrer")
    public String enregistrer(
        @RequestParam(required = false) Integer id,
        @RequestParam String nom,
        @RequestParam(name = "mdp", required = false) String motDePasse,
        @RequestParam String role,
        @RequestParam(required = false) Integer employeeId,
        RedirectAttributes redirection
    ) {
        try {
            utilisateurService.enregistrer(id, nom, motDePasse, role, employeeId);
            redirection.addFlashAttribute("messageSucces", "Le compte a été enregistré.");
            return "redirect:/admin/utilisateurs";
        } catch (IllegalArgumentException exception) {
            redirection.addFlashAttribute("messageErreur", exception.getMessage());
            return id == null
                ? "redirect:/admin/utilisateurs/nouveau"
                : "redirect:/admin/utilisateurs/" + id + "/modifier";
        }
    }

    @PostMapping("/admin/utilisateurs/{id}/supprimer")
    public String supprimer(@PathVariable Integer id, RedirectAttributes redirection) {
        utilisateurService.supprimer(id);
        redirection.addFlashAttribute("messageSucces", "Le compte a été supprimé.");
        return "redirect:/admin/utilisateurs";
    }

    private void preparerFormulaire(Model modele, Utilisateur utilisateur, String titre) {
        modele.addAttribute("utilisateur", utilisateur);
        modele.addAttribute("roles", utilisateurService.listerRoles());
        modele.addAttribute("employees", utilisateurService.listerEmployees());
        modele.addAttribute("titre", titre);
        modele.addAttribute("pageActive", "utilisateurs");
    }
}
