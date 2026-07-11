package com.volyVary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.volyVary.model.Utilisateur;
import com.volyVary.service.UtilisateurService;

@Controller
@RequestMapping
public class UtilisateurMvcController {

    private final UtilisateurService utilisateurService;

    /**
     * Injecte le service responsable des comptes pour maintenir une séparation nette entre
     * les traitements HTTP du contrôleur et les règles de sécurité appliquées aux mots de passe.
     */
    public UtilisateurMvcController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    /**
     * Recherche les comptes selon le nom et le rôle, prépare le Model puis affiche la JSP de liste.
     * L'ancien chemin du template est conservé pour ne pas casser les liens existants.
     */
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
        return "utilisateurs/liste";
    }

    /**
     * Affiche un formulaire vide et fournit les rôles ainsi que les employés disponibles afin
     * que toutes les options soient produites directement par JSTL dans la page JSP.
     */
    @GetMapping("/admin/utilisateurs/nouveau")
    public String nouveau(Model modele) {
        preparerFormulaire(modele, new Utilisateur(), "Créer un compte");
        return "utilisateurs/formulaire";
    }

    /**
     * Charge le compte demandé et prépare les listes nécessaires avant d'afficher le formulaire
     * de modification avec ses valeurs actuelles.
     */
    @GetMapping("/admin/utilisateurs/{id}/modifier")
    public String modifier(@PathVariable Integer id, Model modele) {
        preparerFormulaire(modele, utilisateurService.obtenir(id), "Modifier le compte");
        return "utilisateurs/formulaire";
    }

    /**
     * Enregistre le compte soumis, encode le nouveau mot de passe dans le service et applique le
     * modèle POST-Redirect-GET. Les erreurs de saisie sont transformées en message utilisateur.
     */
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

    /**
     * Supprime le compte demandé par POST et retourne à la liste avec un message temporaire.
     * L'utilisation d'un formulaire permet à Spring Security de vérifier le jeton CSRF.
     */
    @PostMapping("/admin/utilisateurs/{id}/supprimer")
    public String supprimer(@PathVariable Integer id, RedirectAttributes redirection) {
        utilisateurService.supprimer(id);
        redirection.addFlashAttribute("messageSucces", "Le compte a été supprimé.");
        return "redirect:/admin/utilisateurs";
    }

    /**
     * Prépare tous les éléments communs aux formulaires : compte, rôles, employés, titre et page
     * active. La JSP peut ainsi se limiter à l'affichage des valeurs déjà prêtes.
     */
    private void preparerFormulaire(Model modele, Utilisateur utilisateur, String titre) {
        modele.addAttribute("utilisateur", utilisateur);
        modele.addAttribute("roles", utilisateurService.listerRoles());
        modele.addAttribute("employees", utilisateurService.listerEmployees());
        modele.addAttribute("titre", titre);
        modele.addAttribute("pageActive", "utilisateurs");
    }
}
