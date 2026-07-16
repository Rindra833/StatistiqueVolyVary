package com.volyVary.service;

import com.volyVary.dto.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


@Service
public class DistributionExportService {

    private static final String[] ENTETES = {
            "Référence", "Référence client", "Nom", "Prénom", "Date", "Qté vendue", "Total (Ar)", "Statut"
    };

    private static final float[] LARGEURS_COLONNES = {90, 90, 80, 80, 65, 65, 90, 70};

    public byte[] genererExcel(List<FactureResume> factures) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Distributions");

            CellStyle styleEntete = workbook.createCellStyle();
            Font policeEntete = workbook.createFont();
            policeEntete.setBold(true);
            styleEntete.setFont(policeEntete);
            styleEntete.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            styleEntete.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row ligneEntete = sheet.createRow(0);
            for (int i = 0; i < ENTETES.length; i++) {
                Cell cellule = ligneEntete.createCell(i);
                cellule.setCellValue(ENTETES[i]);
                cellule.setCellStyle(styleEntete);
            }

            int numeroLigne = 1;
            for (FactureResume f : factures) {
                Row ligne = sheet.createRow(numeroLigne++);
                ligne.createCell(0).setCellValue(f.getReference());
                ligne.createCell(1).setCellValue(f.getClientReference());
                ligne.createCell(2).setCellValue(f.getClientNom());
                ligne.createCell(3).setCellValue(f.getClientPrenom());
                ligne.createCell(4).setCellValue(f.getDateFormatee());
                ligne.createCell(5).setCellValue(f.getQuantite());
                ligne.createCell(6).setCellValue(f.getMontant());
                ligne.createCell(7).setCellValue(f.getStatut());
            }

            for (int i = 0; i < ENTETES.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] genererPdf(List<FactureResume> factures) throws IOException {
        float margeGauche = 30f;
        float margeHaut = 40f;
        float hauteurLigne = 20f;
        float largeurTitre = 12f;

        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDType1Font policeGras = PDType1Font.HELVETICA_BOLD;
            PDType1Font policeNormale = PDType1Font.HELVETICA;

            PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
            document.addPage(page);
            PDPageContentStream contenu = new PDPageContentStream(document, page);

            float hauteurPage = page.getMediaBox().getHeight();
            float y = hauteurPage - margeHaut;

            
            contenu.beginText();
            contenu.setFont(policeGras, largeurTitre);
            contenu.newLineAtOffset(margeGauche, y);
            contenu.showText("Distribution - liste des factures - VOLY VARY");
            contenu.endText();
            y -= 24;

            contenu.beginText();
            contenu.setFont(policeNormale, 9f);
            contenu.newLineAtOffset(margeGauche, y);
            contenu.showText(factures.size() + " distribution(s)");
            contenu.endText();
            y -= 20;

            y = dessinerEnteteTableau(contenu, policeGras, margeGauche, y, hauteurLigne);

            for (FactureResume f : factures) {
                if (y < margeHaut + hauteurLigne) {
                    contenu.close();
                    page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
                    document.addPage(page);
                    contenu = new PDPageContentStream(document, page);
                    y = hauteurPage - margeHaut;
                    y = dessinerEnteteTableau(contenu, policeGras, margeGauche, y, hauteurLigne);
                }

                String[] valeurs = {
                        f.getReference(), f.getClientReference(), f.getClientNom(), f.getClientPrenom(),
                        f.getDateFormatee(), String.valueOf((long) f.getQuantite()),
                        String.valueOf((long) f.getMontant()), f.getStatut()
                };
                dessinerLigneTableau(contenu, policeNormale, valeurs, margeGauche, y);
                y -= hauteurLigne;
            }

            contenu.close();
            document.save(out);
            return out.toByteArray();
        }
    }

    private float dessinerEnteteTableau(PDPageContentStream contenu, PDType1Font police,
                                         float xDepart, float y, float hauteurLigne) throws IOException {
        dessinerLigneTableau(contenu, police, ENTETES, xDepart, y);

        contenu.setLineWidth(0.5f);
        contenu.moveTo(xDepart, y - 4);
        float largeurTotale = 0;
        for (float l : LARGEURS_COLONNES) largeurTotale += l;
        contenu.lineTo(xDepart + largeurTotale, y - 4);
        contenu.stroke();
        return y - hauteurLigne;
    }

    private void dessinerLigneTableau(PDPageContentStream contenu, PDType1Font police,
                                       String[] valeurs, float xDepart, float y) throws IOException {
        float x = xDepart;
        for (int i = 0; i < valeurs.length; i++) {
            String texte = valeurs[i] == null ? "" : valeurs[i];
            if (texte.length() > 22) {
                texte = texte.substring(0, 21) + "...";
            }
            contenu.beginText();
            contenu.setFont(police, 8.5f);
            contenu.newLineAtOffset(x, y);
            contenu.showText(texte);
            contenu.endText();
            x += LARGEURS_COLONNES[i];
        }
    }
}

