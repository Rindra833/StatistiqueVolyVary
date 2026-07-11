package com.volyVary.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.volyVary.dto.statistique.TableauStatistique;
import com.volyVary.service.StatistiqueService;

@Controller
public class StatistiqueMvcController {

    private final StatistiqueService statistiqueService;

    /**
     * Reçoit le service chargé des calculs. Le contrôleur ne contient volontairement aucune règle
     * mathématique : sa seule responsabilité est de lire le filtre HTTP et de préparer la vue.
     */
    public StatistiqueMvcController(StatistiqueService statistiqueService) {
        this.statistiqueService = statistiqueService;
    }

    /**
     * Affiche les quatre blocs statistiques entre deux dates incluses. Sans date, le service choisit
     * le mois actuel. Une saisie incomplète ou inversée est signalée puis la page revient au mois
     * actuel, ce qui garantit que l'utilisateur obtient toujours un écran exploitable.
     */
    @GetMapping({"/admin/statistiques", "/pages/statistiques/index.html"})
    public String afficher(
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate debut,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate fin,
        Model modele
    ) {
        var tableau = calculerAvecRepli(debut, fin, modele);
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        modele.addAttribute("tableau", tableau);
        modele.addAttribute("debutFormate", formatDate.format(tableau.periode().debut()));
        modele.addAttribute("finFormate", formatDate.format(tableau.periode().fin()));
        modele.addAttribute("pageActive", "statistiques");
        return "statistiques/index";
    }

    /**
     * Demande le calcul au service et intercepte uniquement les erreurs de période prévues. Le repli
     * avec deux valeurs null applique le mois actuel tout en conservant le message pédagogique dans
     * le Model pour expliquer la correction à l'utilisateur.
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
