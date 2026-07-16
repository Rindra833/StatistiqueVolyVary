package com.volyVary.controller;

import com.volyVary.modele.LotPaddy;
import com.volyVary.service.CollecteService;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/collectes")
public class CollecteExportController {

    @Autowired
    private CollecteService collecteService;

    @Autowired
    private CollectePartage collectePartage;

    @GetMapping("/export/csv")
    public void exportCsv(HttpServletResponse response, @RequestParam(required = false) String reference, @RequestParam(required = false) String dateMin, 
    @RequestParam(required = false) String dateMax, @RequestParam(required = false) Double quantiteMin, @RequestParam(required = false) Double quantiteMax,
    @RequestParam(required = false) Double prixMin, @RequestParam(required = false) Double prixMax, @RequestParam(required = false) Double totalMin,
    @RequestParam(required = false) Double totalMax, @RequestParam(required = false) String triePar, @RequestParam(required = false) String ordre) throws IOException {

        List<LotPaddy> lots = collectePartage.getLotsFiltresEtTries(reference, dateMin, dateMax, quantiteMin, quantiteMax, prixMin, prixMax, totalMin, totalMax, triePar, ordre);

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=lots-paddy.csv");

        try (PrintWriter w = response.getWriter()) {
            w.println("Référence;Date;Quantité (kg);Prix unitaire (Ar);Total (Ar)");
            for (LotPaddy lot : lots) {
                w.printf("%s;%s;%.2f;%.0f;%.0f%n",
                    lot.getReference(),
                    lot.getDate(),
                    lot.getQuantite(),
                    lot.getCollecte().getPrixUnitaire(),
                    lot.getPrixCollecte());
            }
        }
    }


    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response, @RequestParam(required = false) String reference, @RequestParam(required = false) String dateMin,
    @RequestParam(required = false) String dateMax, @RequestParam(required = false) Double quantiteMin, @RequestParam(required = false) Double quantiteMax,
    @RequestParam(required = false) Double prixMin, @RequestParam(required = false) Double prixMax, @RequestParam(required = false) Double totalMin,
    @RequestParam(required = false) Double totalMax, @RequestParam(required = false) String triePar, @RequestParam(required = false) String ordre) throws IOException {

        List<LotPaddy> lots = collectePartage.getLotsFiltresEtTries(reference, dateMin, dateMax,quantiteMin, quantiteMax, prixMin, prixMax, totalMin, totalMax, triePar, ordre);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=lots-paddy.xlsx");

        XSSFWorkbook classeur = new XSSFWorkbook();
        XSSFSheet feuille = classeur.createSheet("Lots de paddy");

        String[] entetes = {"Référence", "Date", "Quantité (kg)", "Prix unitaire (Ar)", "Total (Ar)"};
        XSSFRow ligenEntete = feuille.createRow(0);
        for (int i = 0; i < entetes.length; i++) {
            ligenEntete.createCell(i).setCellValue(entetes[i]);
        }

        int numeroLigne = 1;
        for (LotPaddy lot : lots) {
            XSSFRow row = feuille.createRow(numeroLigne++);
            row.createCell(0).setCellValue(lot.getReference());
            row.createCell(1).setCellValue(lot.getDate().toString());
            row.createCell(2).setCellValue(lot.getQuantite());
            row.createCell(3).setCellValue(lot.getCollecte().getPrixUnitaire());
            row.createCell(4).setCellValue(lot.getPrixCollecte());
        }

        for (int i = 0; i < entetes.length; i++) {
            feuille.autoSizeColumn(i);
        }

        classeur.write(response.getOutputStream());
        classeur.close();
    }


    @GetMapping("/export/pdf")
    public void exportPdf(HttpServletResponse response, @RequestParam(required = false) String reference, @RequestParam(required = false) String dateMin,
    @RequestParam(required = false) String dateMax, @RequestParam(required = false) Double quantiteMin, @RequestParam(required = false) Double quantiteMax, @RequestParam(required = false) Double prixMin, 
    @RequestParam(required = false) Double prixMax, @RequestParam(required = false) Double totalMin, @RequestParam(required = false) Double totalMax, @RequestParam(required = false) String triePar,
    @RequestParam(required = false) String ordre) throws IOException, DocumentException {

        List<LotPaddy> lots = collectePartage.getLotsFiltresEtTries(reference, dateMin, dateMax, quantiteMin, quantiteMax, prixMin, prixMax, totalMin, totalMax, triePar, ordre);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=lots-paddy.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        document.add(new Paragraph("Liste des lots de paddy"));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        String[] entetes = {"Référence", "Date", "Quantité (kg)", "Prix unitaire (Ar)", "Total (Ar)"};
        for (String h : entetes) {
            PdfPCell cellule = new PdfPCell(new Phrase(h));
            cellule.setBackgroundColor(new Color(200, 200, 200));
            table.addCell(cellule);
        }

        for (LotPaddy lot : lots) {
            table.addCell(lot.getReference());
            table.addCell(lot.getDate().toString());
            table.addCell(String.format("%.2f", lot.getQuantite()));
            table.addCell(String.format("%.0f", lot.getCollecte().getPrixUnitaire()));
            table.addCell(String.format("%.0f", lot.getPrixCollecte()));
        }

        document.add(table);
        document.close();
    }
}