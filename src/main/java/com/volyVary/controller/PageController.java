package com.volyVary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    /**
     * Redirige l'adresse racine vers la page de connexion. Spring Security redirigera ensuite
     * automatiquement les utilisateurs déjà authentifiés vers leur espace d'administration.
     */
    @GetMapping({"/", "/index.html"})
    public String accueil() {
        return "redirect:/connexion";
    }

    /**
     * Affiche la JSP de connexion. L'ancien chemin du template est conservé comme alias afin
     * que les favoris et les liens déjà présents dans le projet continuent de fonctionner.
     */
    @GetMapping({"/connexion", "/pages/login/index.html"})
    public String connexion() {
        return "connexion";
    }

    /**
     * Présente une page d'erreur simple lorsqu'un utilisateur authentifié tente d'ouvrir une
     * ressource qui ne correspond pas à son rôle.
     */
    @GetMapping("/acces-refuse")
    public String accesRefuse() {
        return "acces-refuse";
    }
}
