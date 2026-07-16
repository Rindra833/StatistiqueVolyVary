package com.volyVary.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import com.volyVary.modele.*;
import com.volyVary.service.*;

import jakarta.servlet.http.HttpServletResponse;

import java.awt.Color;

@RequestMapping("/transformation")
@Controller
public class LotPaddyTransformerController {
    @Autowired
    private LotPaddyTransformerService lotPaddyTransformerService;

    @Autowired
    private DetailLotPaddyTransformeService detailLotPaddyTransformeService;

    @Autowired
    private TransformationService transformationService;

    @GetMapping("/formulaireAjoutTransformation")
    public ModelAndView afficheFormulaire() {
        ModelAndView m = new ModelAndView("transformation/formulaireAjout");
        m.addObject("listeStock", lotPaddyTransformerService.getlisteStockPaddy());
        m.addObject(
            "dateCourante",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
        );
        Transformation tarifCourant = transformationService.getTransformation();
        m.addObject(
            "prixUnitaire",
            tarifCourant == null ? null : tarifCourant.getPrixUnitaire()
        );
        return m;
    }

    /**
     * Valide le formulaire, exécute la transformation puis affiche le détail du lot créé. En cas
     * d'erreur métier, le même formulaire est rendu avec le stock et les valeurs utiles.
     */
    @PostMapping("/traitementAjout")
    public ModelAndView ajouterTransformation(@RequestParam("quantite") double quantite,
            @RequestParam("date") LocalDateTime date,
            @RequestParam("prixUnitaire") Double prixUnitaire) {
        ModelAndView m = new ModelAndView("transformation/DetailsLotPaddy_tranforme");
        ModelAndView retour = new ModelAndView("transformation/formulaireAjout");
        LotPaddyTransforme lotTransforme;
        try {
            lotTransforme = lotPaddyTransformerService.transformation(
                quantite,
                date,
                prixUnitaire
            );

        } catch (IllegalArgumentException e) {
            retour.addObject("listeStock", lotPaddyTransformerService.getlisteStockPaddy());
            retour.addObject("error", e.getMessage());
            retour.addObject("dateCourante", date);
            retour.addObject("prixUnitaire", prixUnitaire);
            return retour;
        }
        List<DetailLotTransforme> details = detailLotPaddyTransformeService
                .getDetailsLotTransforme(lotTransforme.getId());
        m.addObject("listeStock", lotPaddyTransformerService.getlisteStockPaddy());
        m.addObject("saisie", quantite);
        m.addObject("details", details);
        m.addObject("LotTouche", lotPaddyTransformerService.getLotPaddyTouche(lotTransforme.getId()));
        m.addObject("lotpaddyTransforme", lotTransforme);
        return m;
    }

    @GetMapping("/lotPaddyTransforme")
    public ModelAndView afficheFormulaireHistorique(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        ModelAndView m = new ModelAndView("transformation/LotPaddy_transforme");
        int safePage = Math.max(page, 0);
        String safeSortBy = "date";
        if ("reference".equals(sortBy)
            || "date".equals(sortBy)
            || "quantite".equals(sortBy)
            || "prixTransformation".equals(sortBy)) {
            safeSortBy = sortBy;
        }
        Sort sort;
        if ("asc".equalsIgnoreCase(sortDir)) {
            sort = Sort.by(safeSortBy).ascending();
        } else {
            sort = Sort.by(safeSortBy).descending();
        }
        Pageable pageable = PageRequest.of(safePage, 9, sort);

        Page<LotPaddyTransforme> lots = lotPaddyTransformerService.getAll(pageable);

        m.addObject("transformation", transformationService.getTransformation());
        m.addObject("lotPaddyTransforme", lots);

        Double total = lotPaddyTransformerService.totalPaddyTransformer();
        if (total == null) {
            m.addObject("lotPaddyVide", "aucun paddy transformer");
        } else {
            m.addObject("total", lotPaddyTransformerService.totalPaddyTransformer());
        }

        return m;
    }

    @GetMapping("/traitementFiltre")
    public ModelAndView traitementFiltre(
            @RequestParam(required = false) String debut,
            @RequestParam(required = false) String fin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        ModelAndView m = new ModelAndView("transformation/LotPaddy_transforme");
        int safePage = Math.max(page, 0);
        String safeSortBy = "date";
        if ("reference".equals(sortBy)
            || "date".equals(sortBy)
            || "quantite".equals(sortBy)
            || "prixTransformation".equals(sortBy)) {
            safeSortBy = sortBy;
        }
        Sort sort;
        if ("asc".equalsIgnoreCase(sortDir)) {
            sort = Sort.by(safeSortBy).ascending();
        } else {
            sort = Sort.by(safeSortBy).descending();
        }
        Pageable pageable = PageRequest.of(safePage, 9, sort);

        LocalDateTime debutFiltre = parseDateTimeOrNull(debut);
        LocalDateTime finFiltre = parseDateTimeOrNull(fin);

        if ((debut != null && !debut.isBlank() && debutFiltre == null)
                || (fin != null && !fin.isBlank() && finFiltre == null)) {
            m.addObject("error", "Format de date invalide pour le filtre");
        }

        Page<LotPaddyTransforme> lots = lotPaddyTransformerService.filtrePaddyTransforme(
                debutFiltre,
                finFiltre,
                pageable);
        m.addObject("lotPaddyTransforme", lots);
        String debutValue = "";
        if (debut != null) {
            debutValue = debut;
        }
        String finValue = "";
        if (fin != null) {
            finValue = fin;
        }
        m.addObject("debut", debutValue);
        m.addObject("fin", finValue);
        m.addObject("transformation",
                transformationService.getTransformation());
        Double total = lotPaddyTransformerService.totalPaddyTransformer();

        if (total == null) {
            m.addObject("lotPaddyVide",
                    "aucun paddy transformer");
        } else {
            m.addObject("total", total);
        }
        return m;
    }

    private LocalDateTime parseDateTimeOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/pdfListe")
    public void exportPdfListe(
            @RequestParam(required = false) String debut,
            @RequestParam(required = false) String fin,
            HttpServletResponse response)
            throws IOException, DocumentException {
        LocalDateTime debutFiltre = parseDateTimeOrNull(debut);
        LocalDateTime finFiltre = parseDateTimeOrNull(fin);

        Page<LotPaddyTransforme> lotsPage = lotPaddyTransformerService.filtrePaddyTransforme(
                debutFiltre,
                finFiltre,
                Pageable.unpaged());
        List<LotPaddyTransforme> lots = lotsPage.getContent();

        Transformation transformation = transformationService.getTransformation();
        double prixUnitaire = 0.0;
        if (transformation != null) {
            prixUnitaire = transformation.getPrixUnitaire();
        }
        double totalQuantite = lotPaddyTransformerService.totalPaddyTransformer(lots);
        double totalMontant = lots.stream().mapToDouble(LotPaddyTransforme::getPrixTransformation).sum();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=liste-transformations.pdf");

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titre = new Font(Font.HELVETICA, 16, Font.BOLD, Color.BLUE);
        Font normal = new Font(Font.HELVETICA, 10);
        Font gras = new Font(Font.HELVETICA, 10, Font.BOLD);

        Paragraph pTitre = new Paragraph("LISTE DES TRANSFORMATIONS DE PADDY", titre);
        pTitre.setAlignment(Element.ALIGN_CENTER);
        document.add(pTitre);
        document.add(new Paragraph(" "));

        String periode = "Toutes les dates";
        if (debutFiltre != null && finFiltre != null) {
            periode = "Du " + debutFiltre + " au " + finFiltre;
        } else if (debutFiltre != null) {
            periode = "A partir de " + debutFiltre;
        } else if (finFiltre != null) {
            periode = "Jusqu'au " + finFiltre;
        }
        document.add(new Paragraph("Periode: " + periode, normal));
        document.add(new Paragraph("Nombre de lots: " + lots.size(), normal));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new int[] { 20, 28, 14, 18, 20 });

        PdfPCell h1 = new PdfPCell(new Phrase("Reference", gras));
        PdfPCell h2 = new PdfPCell(new Phrase("Date", gras));
        PdfPCell h3 = new PdfPCell(new Phrase("Quantite (kg)", gras));
        PdfPCell h4 = new PdfPCell(new Phrase("Prix unitaire (Ar)", gras));
        PdfPCell h5 = new PdfPCell(new Phrase("Total (Ar)", gras));

        h1.setBackgroundColor(Color.LIGHT_GRAY);
        h2.setBackgroundColor(Color.LIGHT_GRAY);
        h3.setBackgroundColor(Color.LIGHT_GRAY);
        h4.setBackgroundColor(Color.LIGHT_GRAY);
        h5.setBackgroundColor(Color.LIGHT_GRAY);

        h1.setHorizontalAlignment(Element.ALIGN_CENTER);
        h2.setHorizontalAlignment(Element.ALIGN_CENTER);
        h3.setHorizontalAlignment(Element.ALIGN_CENTER);
        h4.setHorizontalAlignment(Element.ALIGN_CENTER);
        h5.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(h1);
        table.addCell(h2);
        table.addCell(h3);
        table.addCell(h4);
        table.addCell(h5);

        if (lots.isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase("Aucune transformation trouvee", normal));
            empty.setColspan(5);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            empty.setPadding(8);
            table.addCell(empty);
        } else {
            for (LotPaddyTransforme lot : lots) {
                PdfPCell cRef = new PdfPCell(new Phrase(lot.getReference(), normal));
                PdfPCell cDate = new PdfPCell(new Phrase(String.valueOf(lot.getDate()), normal));
                PdfPCell cQty = new PdfPCell(new Phrase(String.valueOf(lot.getQuantite()), normal));
                PdfPCell cUnit = new PdfPCell(new Phrase(String.format("%,.2f", prixUnitaire), normal));
                PdfPCell cTotal = new PdfPCell(new Phrase(String.format("%,.2f", lot.getPrixTransformation()), normal));

                cRef.setPadding(5);
                cDate.setPadding(5);
                cQty.setPadding(5);
                cUnit.setPadding(5);
                cTotal.setPadding(5);

                cQty.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cUnit.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(cRef);
                table.addCell(cDate);
                table.addCell(cQty);
                table.addCell(cUnit);
                table.addCell(cTotal);
            }
        }

        document.add(table);
        document.add(new Paragraph(" "));

        PdfPTable resume = new PdfPTable(2);
        resume.setWidthPercentage(50);
        resume.setHorizontalAlignment(Element.ALIGN_RIGHT);
        resume.setWidths(new int[] { 55, 45 });

        PdfPCell r1 = new PdfPCell(new Phrase("Total quantite (kg)", gras));
        PdfPCell r2 = new PdfPCell(new Phrase(String.format("%,.2f", totalQuantite), normal));
        PdfPCell r3 = new PdfPCell(new Phrase("Total montant (Ar)", gras));
        PdfPCell r4 = new PdfPCell(new Phrase(String.format("%,.2f", totalMontant), normal));

        r2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        r4.setHorizontalAlignment(Element.ALIGN_RIGHT);

        resume.addCell(r1);
        resume.addCell(r2);
        resume.addCell(r3);
        resume.addCell(r4);

        document.add(resume);
        document.close();
    }

    @GetMapping("/searchReference")
    public ModelAndView searchReference(
            @RequestParam("reference") String reference,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        ModelAndView m = new ModelAndView("transformation/LotPaddy_transforme");

        int safePage = Math.max(page, 0);
        String safeSortBy = "date";
        if ("reference".equals(sortBy)
            || "date".equals(sortBy)
            || "quantite".equals(sortBy)
            || "prixTransformation".equals(sortBy)) {
            safeSortBy = sortBy;
        }
        Sort sort;
        if ("asc".equalsIgnoreCase(sortDir)) {
            sort = Sort.by(safeSortBy).ascending();
        } else {
            sort = Sort.by(safeSortBy).descending();
        }
        Pageable pageable = PageRequest.of(safePage, 9, sort);
        String safeReference = "";
        if (reference != null) {
            safeReference = reference.trim();
        }
        Page<LotPaddyTransforme> lotsPage = lotPaddyTransformerService.search(
                safeReference,
                pageable);

        m.addObject("lotPaddyTransforme", lotsPage);
        m.addObject("transformation", transformationService.getTransformation());
        m.addObject("reference", reference);

        if (lotsPage.isEmpty()) {
            m.addObject("message", "Aucun resultat pour la reference recherchee");
        }

        Double total = lotPaddyTransformerService.totalPaddyTransformer();
        if (total == null) {
            m.addObject("lotPaddyVide", "aucun paddy transformer");
        } else {
            m.addObject("total", total);
        }

        return m;
    }
}
