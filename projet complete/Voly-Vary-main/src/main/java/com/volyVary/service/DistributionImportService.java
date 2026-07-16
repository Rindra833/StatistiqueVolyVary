package com.volyVary.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


@Service
public class DistributionImportService {

    public Map<String, Object> lireExcel(MultipartFile fichier) {
        Map<String, Object> data = new HashMap<>();

        if (fichier.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }
        if (fichier.getOriginalFilename() == null || !fichier.getOriginalFilename().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Le fichier doit être au format Excel (.xlsx)");
        }

        try (InputStream is = fichier.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() < 2) {
                throw new IllegalArgumentException("Le fichier Excel est vide ou ne contient pas de données");
            }

            Row row = sheet.getRow(1);
            if (row == null) {
                throw new IllegalArgumentException("Aucune donnée trouvée dans le fichier");
            }

            data.put("refClient", lireCelluleTexte(row, 0, "Référence client"));
            data.put("nom", lireCelluleTexte(row, 1, "Nom"));
            data.put("prenom", lireCelluleTexte(row, 2, "Prénom"));
            data.put("telephone", lireCelluleTelephone(row, 3));
            data.put("produit", lireCelluleTexte(row, 4, "Produit"));
            data.put("quantite", lireCelluleNombre(row, 5, "Quantité"));
            data.put("lieu", lireCelluleTexte(row, 6, "Lieu"));
            data.put("livreur", lireCelluleTexte(row, 7, "Livreur"));

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Erreur lors de la lecture du fichier : " + e.getMessage());
        }

        return data;
    }

    private String lireCelluleTexte(Row row, int colonne, String nomChamp) {
        Cell cell = row.getCell(colonne);
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            throw new IllegalArgumentException("Le champ '" + nomChamp + "' est manquant");
        }
        String valeur = cell.getStringCellValue().trim();
        if (valeur.isEmpty()) {
            throw new IllegalArgumentException("Le champ '" + nomChamp + "' est vide");
        }
        return valeur;
    }

    private String lireCelluleTelephone(Row row, int colonne) {
        Cell cell = row.getCell(colonne);
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            throw new IllegalArgumentException("Le champ 'Téléphone' est manquant");
        }
        String telephone;
        if (cell.getCellType() == CellType.NUMERIC) {
            telephone = String.valueOf((long) cell.getNumericCellValue());
        } else {
            telephone = cell.getStringCellValue().trim();
        }
        if (telephone.isEmpty()) {
            throw new IllegalArgumentException("Le champ 'Téléphone' est vide");
        }
        return telephone;
    }

    private Double lireCelluleNombre(Row row, int colonne, String nomChamp) {
        Cell cell = row.getCell(colonne);
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            throw new IllegalArgumentException("Le champ '" + nomChamp + "' est manquant");
        }
        if (cell.getCellType() != CellType.NUMERIC) {
            throw new IllegalArgumentException("Le champ '" + nomChamp + "' doit être un nombre");
        }
        double valeur = cell.getNumericCellValue();
        if (valeur <= 0) {
            throw new IllegalArgumentException("Le champ '" + nomChamp + "' doit être positif (reçu : " + valeur + ")");
        }
        return valeur;
    }
}
