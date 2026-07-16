package com.volyVary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.volyVary.modele.*;
import com.volyVary.service.*;

import jakarta.servlet.http.HttpServletResponse;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.IOException;
import java.util.List;

@RequestMapping("/transformation")
@Controller
public class DetailLotPaddyTransformeController {
    @Autowired
    private DetailLotPaddyTransformeService detailLotPaddyTransformeService;

    @Autowired
    private LotPaddyTransformerService lotPaddyTransformerService;

    @GetMapping("/detailsLotPaddyTransforme")
    public ModelAndView afficheDetailLotTransforme(@RequestParam("idLotTransforme") int idLotTransforme) {
        ModelAndView m = new ModelAndView("transformation/DetailsLotPaddy_tranforme");
        LotPaddyTransforme lotTransforme = lotPaddyTransformerService.getLotPaddyTransformeById(idLotTransforme);

        if (lotTransforme == null) {
            m.addObject("error", "Aucun detail trouve pour cette transformation");
            return m;
        }

        List<DetailLotTransforme> details = detailLotPaddyTransformeService.getDetailsLotTransforme(idLotTransforme);
        m.addObject("details", details);
        m.addObject("saisie", lotTransforme.getQuantite());
        m.addObject("LotTouche", lotPaddyTransformerService.getLotPaddyTouche(idLotTransforme));
        m.addObject("lotpaddyTransforme", lotTransforme);
        return m;
    }

    @GetMapping("/pdf/{id}")
    public void exportPdf(
            @PathVariable int id,
            HttpServletResponse response)
            throws IOException, DocumentException {
        List<DetailLotTransforme> details = detailLotPaddyTransformeService.getDetailsLotTransforme(id);
        LotPaddyTransforme lotpaddyTransforme = lotPaddyTransformerService.getLotPaddyTransformeById(id);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=transformation.pdf");

        Document document = new Document();

        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        document.open();

        // ===== Les polices =====
        Font titre = new Font(Font.HELVETICA, 18, Font.BOLD, Color.BLUE);
        Font sousTitre = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font normal = new Font(Font.HELVETICA, 11);
        Font gras = new Font(Font.HELVETICA, 11, Font.BOLD);

        // ===== Titre =====
        Paragraph pTitre = new Paragraph("RAPPORT DE TRANSFORMATION", titre);
        pTitre.setAlignment(Element.ALIGN_CENTER);
        document.add(pTitre);

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        // ===== Informations =====
        PdfPTable info = new PdfPTable(2);
        info.setWidthPercentage(100);
        info.setWidths(new int[] { 35, 65 });

        info.addCell(new Phrase("Date", gras));
        info.addCell(new Phrase(details.get(0).getDate().toString(), normal));

        info.addCell(new Phrase("Référence", gras));
        info.addCell(new Phrase(details.get(0).getLotTransforme().getReference(), normal));

        info.addCell(new Phrase("Quantité de Paddy", gras));
        info.addCell(new Phrase(lotpaddyTransforme.getQuantite() + " kg", normal));

        document.add(info);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Produits obtenus", sousTitre));
        document.add(new Paragraph(" "));

        // ===== Tableau des produits =====
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(80);
        table.setWidths(new int[] { 70, 30 });

        // Entêtes
        PdfPCell cell1 = new PdfPCell(new Phrase("Produit", gras));
        cell1.setBackgroundColor(Color.LIGHT_GRAY);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell cell2 = new PdfPCell(new Phrase("Quantité (kg)", gras));
        cell2.setBackgroundColor(Color.LIGHT_GRAY);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(cell1);
        table.addCell(cell2);

        // Données
        for (DetailLotTransforme d : details) {

            PdfPCell produit = new PdfPCell(new Phrase(d.getProduit().getNomProduit()));
            produit.setPadding(5);

            PdfPCell quantite = new PdfPCell(new Phrase(String.valueOf(d.getQuantite())));
            quantite.setHorizontalAlignment(Element.ALIGN_CENTER);
            quantite.setPadding(5);

            table.addCell(produit);
            table.addCell(quantite);
        }

        document.add(table);

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        // ===== Résumé =====
        PdfPTable resume = new PdfPTable(2);
        resume.setWidthPercentage(100);

        resume.addCell(new Phrase("Prix de transformation / kg", gras));
        resume.addCell(new Phrase(lotpaddyTransforme.getPrixTransformation() + " Ar"));

        resume.addCell(new Phrase("Montant total", gras));
        resume.addCell(new Phrase(
                lotpaddyTransforme.getPrixTransformation() * lotpaddyTransforme.getQuantite()
                        + " Ar"));

        document.add(resume);

        document.close();

    }
}
