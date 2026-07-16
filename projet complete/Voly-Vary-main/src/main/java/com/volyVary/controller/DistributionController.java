package com.volyVary.controller;

import com.volyVary.modele.*;
import com.volyVary.service.*;
import com.volyVary.dto.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/distribution")
public class DistributionController {

    @Autowired
    private DistributionService distributionService;

    @Autowired
    private DistributionExportService distributionExportService;

    @Autowired
    private DistributionImportService distributionImportService;

    @Autowired
    private ClientService clientService;


    @GetMapping("/nouvelleDistribution")
    public String afficherPriseCommande(Model model) {
        model.addAttribute("produits", distributionService.chargerProduits());
        model.addAttribute("lieux", distributionService.chargerLieux());
        model.addAttribute("livreurs", distributionService.chargerLivreurs());
        return "distribution/nouvelle/index";
    }

    @GetMapping("/rechercherClient")
    @ResponseBody
    public org.springframework.http.ResponseEntity<Client> rechercherClient(@RequestParam String ref) {
        Client client = clientService.getClientParReference(ref);
        if (client == null) {
            return org.springframework.http.ResponseEntity.notFound().build();
        }
        return org.springframework.http.ResponseEntity.ok(client);
    }

    @PostMapping("/validerCommande")
    public String validerCommande(@RequestParam String refClient,
                                  @RequestParam String nom,
                                  @RequestParam String prenom,
                                  @RequestParam String tel,
                                  @RequestParam int idLieu,
                                  @RequestParam int idLivreur,
                                  @RequestParam List<Integer> idsProduits,
                                  @RequestParam List<Double> quantites) {
        Client client = new Client();
        client.setReference(refClient);
        client.setNom(nom);
        client.setPrenom(prenom);
        client.setTelephone(tel);
        client.setDate(LocalDate.now());

        List<LigneCommande> lignes = construireLignes(idsProduits, quantites);
        Distribution distribution = distributionService.validerCommande(client, idLieu, idLivreur, lignes);
        return "redirect:/distribution/facture/" + distribution.getId();
    }

    @GetMapping("/facture/{id}")
    public String afficherFacture(@PathVariable int id, Model model) {
        com.volyVary.modele.Distribution distribution = distributionService.chargerFacture(id);
        model.addAttribute("distribution", distribution);
        model.addAttribute("details", distributionService.chargerDetails(id));
        model.addAttribute("total", distributionService.calculerTotalFacture(id));
        model.addAttribute("statut", distributionService.obtenirStatutActuel(id));
        model.addAttribute("dateFacture", distribution.getDate() != null
                ? distribution.getDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "-");
        return "distribution/facture";
    }

    @PostMapping("/terminerCommande/{id}")
    public String terminerCommande(@PathVariable int id) {
        distributionService.terminerCommande(id);
        return "redirect:/distribution/listeFactures";
    }

    @PostMapping("/annulerCommande/{id}")
    public String annulerCommande(@PathVariable int id) {
        distributionService.annulerCommande(id);
        return "redirect:/distribution/listeFactures";
    }

    @GetMapping("/listeFactures")
    public String afficherListeFactures(
            @RequestParam(name = "q", required = false) String recherche,
            @RequestParam(required = false) String statut,
            @RequestParam(defaultValue = "reference") String tri,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int taille,
            Model model) {

        org.springframework.data.domain.Page<FactureResume> pageResultat =
                distributionService.rechercherFactures(recherche, statut, tri, dir, page, taille);

        // Totaux globaux (independants des filtres/pagination courants)
        long enCours = distributionService.listerFacturesResume().stream()
                .filter(f -> "En cours".equals(f.getStatut())).count();

        model.addAttribute("factures", pageResultat.getContent());
        model.addAttribute("pageCourante", pageResultat.getNumber());
        model.addAttribute("pageTotales", pageResultat.getTotalPages());
        model.addAttribute("totalElements", pageResultat.getTotalElements());
        model.addAttribute("q", recherche == null ? "" : recherche);
        model.addAttribute("statut", statut == null ? "" : statut);
        model.addAttribute("tri", tri);
        model.addAttribute("dir", dir);
        model.addAttribute("taille", taille);
        model.addAttribute("nombreTotalFactures", distributionService.listerFacturesResume().size());
        model.addAttribute("qteGlobale", distributionService.calculerQteGlobale());
        model.addAttribute("recetteTotale", distributionService.calculerRecetteTotale());
        model.addAttribute("nombreEnCours", enCours);
        return "distribution/listeFactures";
    }

    @GetMapping("/export/csv")
    public void exporterCsv(
            @RequestParam(name = "q", required = false) String recherche,
            @RequestParam(required = false) String statut,
            @RequestParam(defaultValue = "reference") String tri,
            @RequestParam(defaultValue = "asc") String dir,
            jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {

        List<FactureResume> factures =
                distributionService.facturesFiltrees(recherche, statut, tri, dir);

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=distributions.csv");

        try (java.io.PrintWriter w = response.getWriter()) {
            w.println("Référence;Référence client;Nom;Prénom;Date;Qté vendue;Total (Ar);Statut");
            for (FactureResume f : factures) {
                w.printf("%s;%s;%s;%s;%s;%.0f;%.0f;%s%n",
                        f.getReference(), f.getClientReference(), f.getClientNom(), f.getClientPrenom(),
                        f.getDateFormatee(), f.getQuantite(), f.getMontant(), f.getStatut());
            }
        }
    }

    @GetMapping("/export/excel")
    public org.springframework.http.ResponseEntity<byte[]> exporterExcel(
            @RequestParam(name = "q", required = false) String recherche,
            @RequestParam(required = false) String statut,
            @RequestParam(defaultValue = "reference") String tri,
            @RequestParam(defaultValue = "asc") String dir) throws java.io.IOException {

        List<FactureResume> factures =
                distributionService.facturesFiltrees(recherche, statut, tri, dir);
        byte[] fichier = distributionExportService.genererExcel(factures);

        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"distributions.xlsx\"")
                .contentType(org.springframework.http.MediaType
                        .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(fichier);
    }

    @GetMapping("/export/pdf")
    public org.springframework.http.ResponseEntity<byte[]> exporterPdf(
            @RequestParam(name = "q", required = false) String recherche,
            @RequestParam(required = false) String statut,
            @RequestParam(defaultValue = "reference") String tri,
            @RequestParam(defaultValue = "asc") String dir) throws java.io.IOException {

        List<FactureResume> factures =
                distributionService.facturesFiltrees(recherche, statut, tri, dir);
        byte[] fichier = distributionExportService.genererPdf(factures);

        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"distributions.pdf\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(fichier);
    }

    @GetMapping("/voirDetail/{id}")
    public String voirDetail(@PathVariable int id) {
        return "redirect:/distribution/facture/" + id;
    }

    @PostMapping("/lire-excel")
    @ResponseBody
    public java.util.Map<String, Object> lireExcel(@org.springframework.web.bind.annotation.RequestParam("fichier") org.springframework.web.multipart.MultipartFile fichier) {
        try {
            return distributionImportService.lireExcel(fichier);
        } catch (IllegalArgumentException e) {
            java.util.Map<String, Object> erreur = new java.util.HashMap<>();
            erreur.put("erreur", e.getMessage());
            return erreur;
        }
    }

    private List<LigneCommande> construireLignes(List<Integer> ids, List<Double> qtes) {
        List<LigneCommande> lignes = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            lignes.add(new LigneCommande(ids.get(i), qtes.get(i)));
        }
        return lignes;
    }
}