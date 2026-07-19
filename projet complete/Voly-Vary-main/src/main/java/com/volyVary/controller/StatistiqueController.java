package com.volyVary.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.volyVary.dto.*;
import com.volyVary.service.*;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class StatistiqueController {

    private final StatistiqueService statistiqueService;

    /**
     * Reçoit le service de calcul. Les paramètres HTTP et le Model de la JSP.
     */
    public StatistiqueController(StatistiqueService statistiqueService) {
        this.statistiqueService = statistiqueService;
    }

    /**
     * Recharge les statistiques depuis PostgreSQL à chaque requête GET. L'en-tête no-store empêche
     * le navigateur de réafficher une ancienne version du dashboard après une nouvelle opération.
     */
    @GetMapping("/admin/statistiques")
    public String afficher(
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate debut,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate fin,
        Model modele,
        HttpServletResponse reponse
    ) {
        reponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        var tableau = calculerAvecRepli(debut, fin, modele);
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        modele.addAttribute("tableau", tableau);
        modele.addAttribute("debutFormate", formatDate.format(tableau.periode().debut()));
        modele.addAttribute("finFormate", formatDate.format(tableau.periode().fin()));
        modele.addAttribute("pageActive", "statistiques");
        return "statistiques/index";
    }

    /**
     * Intercepte uniquement les erreurs attendues du filtre. En cas de dates invalides, la page
     * affiche l'explication et revient au mois actuel au lieu de produire une erreur serveur.
     */
    private TableauStatistique calculerAvecRepli(
        LocalDate debut,
        LocalDate fin,
        Model modele
    ) {
        try {
            return statistiqueService.calculerTableau(debut, fin);
        } catch (IllegalArgumentException erreur) {
            modele.addAttribute("messageErreur", erreur.getMessage());
            return statistiqueService.calculerTableau(null, null);
        }
    }
}
