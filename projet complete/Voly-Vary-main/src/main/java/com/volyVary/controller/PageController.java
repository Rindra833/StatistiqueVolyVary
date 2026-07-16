package com.volyVary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/")
    public String accueil() {
        return "redirect:/connexion";
    }

    @GetMapping("/connexion")
    public String connexion() {
        return "connexion";
    }

    @GetMapping("/acces-refuse")
    public String accesRefuse() {
        return "acces-refuse";
    }
}
