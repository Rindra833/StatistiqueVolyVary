package com.volyVary.controller;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import com.volyVary.modele.*;
import com.volyVary.service.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class TransactionController {
    private static final String FORM_STATE_KEY = "transactionFormState";

    @Autowired
    private TypeTransactionService typeTransactionService;

    @Autowired
    private CategorieFournitureService categorieFournitureService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private FournitureCatalogService fournitureCatalogService;

    @Autowired
    private ClientService clientService;

    // ===== Formulaire =====

    @GetMapping( "/transaction/form")
    public String afficherFormulaireTransaction(Model modele, HttpSession session) {
        session.removeAttribute(FORM_STATE_KEY);
        List<TypeTransaction> typesTransaction = typeTransactionService.getTypesTransaction();
        List<CategorieFourniture> categorieFournitures = categorieFournitureService.getCategories();

        modele.addAttribute("listeTypesTransaction", typesTransaction);
        modele.addAttribute("listeCategoriesFourniture", categorieFournitures);
        modele.addAttribute("dateCourante", LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        modele.addAttribute("referenceTransaction", transactionService.genererReferenceTransaction());
        modele.addAttribute("typeTransactionParDefautId", typesTransaction.isEmpty() ? null : typesTransaction.get(0).getId());
        modele.addAttribute("formLignes", new ArrayList<Map<String, Object>>());
        modele.addAttribute("formFournitures", new ArrayList<Map<String, Object>>());
        modele.addAttribute("typeTransactionId", typesTransaction.isEmpty() ? null : typesTransaction.get(0).getId());
        modele.addAttribute("categorieFournitureId", null);

        return "transaction/form";
    }

    @PostMapping("/transaction/ajouter-ligne")
    public String ajouterLigne(
        @RequestParam(value = "referenceTransaction", required = false) String referenceTransaction,
        @RequestParam(value = "dateTransaction", required = false) String dateTransaction,
        @RequestParam(value = "typeTransactionId", required = false) Integer typeTransactionId,
        @RequestParam(value = "referenceClient", required = false) String referenceClient,
        @RequestParam(value = "nomClient", required = false) String nomClient,
        @RequestParam(value = "prenomClient", required = false) String prenomClient,
        @RequestParam(value = "telephoneClient", required = false) String telephoneClient,
        @RequestParam(value = "categorieFournitureId", required = false) Integer categorieFournitureId,
        @RequestParam(value = "idFourniture", required = false) List<Integer> idFournitures,
        @RequestParam(value = "quantite", required = false) List<Integer> quantites,
        HttpSession session,
        Model modele
    ) {
        List<Map<String, Object>> lignes = transactionService.reconstruireLignes(idFournitures, quantites);
        Map<String, Object> nouvelleLigne = new HashMap<>();
        nouvelleLigne.put("idFourniture", null);
        nouvelleLigne.put("quantite", 1);
        lignes.add(nouvelleLigne);
        remplirModeleFormulaire(modele, session, typeTransactionId, referenceClient, nomClient, prenomClient, telephoneClient, categorieFournitureId, referenceTransaction, dateTransaction, lignes);
        return "transaction/form";
    }

    @PostMapping("/transaction/retirer-ligne")
    public String retirerLigne(
        @RequestParam(value = "referenceTransaction", required = false) String referenceTransaction,
        @RequestParam(value = "dateTransaction", required = false) String dateTransaction,
        @RequestParam(value = "typeTransactionId", required = false) Integer typeTransactionId,
        @RequestParam(value = "referenceClient", required = false) String referenceClient,
        @RequestParam(value = "nomClient", required = false) String nomClient,
        @RequestParam(value = "prenomClient", required = false) String prenomClient,
        @RequestParam(value = "telephoneClient", required = false) String telephoneClient,
        @RequestParam(value = "categorieFournitureId", required = false) Integer categorieFournitureId,
        @RequestParam(value = "idFourniture", required = false) List<Integer> idFournitures,
        @RequestParam(value = "quantite", required = false) List<Integer> quantites,
        @RequestParam(value = "removeLine") int removeLine,
        HttpSession session,
        Model modele
    ) {
        List<Map<String, Object>> lignes = transactionService.reconstruireLignes(idFournitures, quantites);
        if (removeLine >= 0 && removeLine < lignes.size()) {
            lignes.remove(removeLine);
        }
        remplirModeleFormulaire(modele, session, typeTransactionId, referenceClient, nomClient, prenomClient, telephoneClient, categorieFournitureId, referenceTransaction, dateTransaction, lignes);
        return "transaction/form";
    }

    @PostMapping("/transaction/charger-fournitures")
    public String chargerFournitures(
        @RequestParam(value = "referenceTransaction", required = false) String referenceTransaction,
        @RequestParam(value = "dateTransaction", required = false) String dateTransaction,
        @RequestParam(value = "typeTransactionId", required = false) Integer typeTransactionId,
        @RequestParam(value = "referenceClient", required = false) String referenceClient,
        @RequestParam(value = "nomClient", required = false) String nomClient,
        @RequestParam(value = "prenomClient", required = false) String prenomClient,
        @RequestParam(value = "telephoneClient", required = false) String telephoneClient,
        @RequestParam(value = "categorieFournitureId", required = false) Integer categorieFournitureId,
        @RequestParam(value = "idFourniture", required = false) List<Integer> idFournitures,
        @RequestParam(value = "quantite", required = false) List<Integer> quantites,
        HttpSession session,
        Model modele
    ) {
        List<Map<String, Object>> lignes = transactionService.reconstruireLignes(idFournitures, quantites);

        /*
         * Au premier chargement d'une catégorie, le formulaire doit afficher directement une ligne
         * à renseigner. Sans cette ligne, l'utilisateur pouvait envoyer un formulaire sans produit
         * et tomber ensuite sur une redirection d'erreur.
         */
        if (lignes.isEmpty() && categorieFournitureId != null
            && !fournitureCatalogService.getFournituresParCategorie(categorieFournitureId).isEmpty()) {
            Map<String, Object> nouvelleLigne = new HashMap<>();
            nouvelleLigne.put("idFourniture", null);
            nouvelleLigne.put("quantite", 1);
            lignes.add(nouvelleLigne);
        }

        remplirModeleFormulaire(modele, session, typeTransactionId, referenceClient, nomClient, prenomClient, telephoneClient, categorieFournitureId, referenceTransaction, dateTransaction, lignes);
        return "transaction/form";
    }

    @PostMapping("/transaction/importer-fichier")
    public String importerFichier(
        @RequestParam(value = "referenceTransaction", required = false) String referenceTransaction,
        @RequestParam(value = "dateTransaction", required = false) String dateTransaction,
        @RequestParam(value = "typeTransactionId", required = false) Integer typeTransactionId,
        @RequestParam("fichier") MultipartFile fichier,
        HttpSession session,
        Model modele
    ) {
        String nomClient = "";
        String prenomClient = "";
        String telephoneClient = "";
        String referenceClient = "";
        Integer categorieFournitureId = null;
        List<Map<String, Object>> lignes = new ArrayList<>();

        try {
            List<String[]> lignesFichier;
            String nomFichier = fichier.getOriginalFilename();
            if (nomFichier != null && (nomFichier.endsWith(".xlsx") || nomFichier.endsWith(".xls"))) {
                lignesFichier = lireExcel(fichier);
            } else {
                lignesFichier = lireCsv(fichier);
            }

            if (!lignesFichier.isEmpty()) {
                int debut = 0;
                String[] premiereLigne = lignesFichier.get(0);
                if (premiereLigne.length > 0) {
                    String premier = premiereLigne[0].trim().toLowerCase();
                    if (premier.contains("reference") || premier.contains("client") || premier.contains("fourniture")) {
                        debut = 1;
                    }
                }

                String refClientTrouve = null;
                List<String[]> produitsBruts = new ArrayList<>();
                for (int i = debut; i < lignesFichier.size(); i++) {
                    String[] champs = lignesFichier.get(i);
                    if (champs.length < 2) continue;
                    String refClient = champs[0].trim();
                    String refFourniture = champs[1].trim();
                    String quantiteStr = champs.length >= 3 ? champs[2].trim() : "1";
                    if (refClient.isEmpty() || refFourniture.isEmpty()) continue;
                    if (refClientTrouve == null) refClientTrouve = refClient;
                    produitsBruts.add(new String[]{refClient, refFourniture, quantiteStr});
                }

                if (refClientTrouve != null) {
                    referenceClient = refClientTrouve;
                    Client client = clientService.getClientParReference(referenceClient);
                    if (client != null) {
                        nomClient = client.getNom();
                        prenomClient = client.getPrenom();
                        telephoneClient = client.getTelephone();
                    }
                }

                Map<String, Fourniture> mapFournitures = fournitureCatalogService.construireMapFournituresParRef();

                for (String[] produit : produitsBruts) {
                    String refFourniture = produit[1];
                    Fourniture fourniture = mapFournitures.get(refFourniture);
                    if (fourniture == null) continue;
                    if (categorieFournitureId == null) {
                        categorieFournitureId = fourniture.getIdCategorie();
                    }
                    int quantite;
                    try {
                        quantite = Integer.parseInt(produit[2]);
                        if (quantite < 1) quantite = 1;
                    } catch (NumberFormatException e) {
                        quantite = 1;
                    }
                    Map<String, Object> ligne = new HashMap<>();
                    ligne.put("idFourniture", fourniture.getId());
                    ligne.put("quantite", quantite);
                    lignes.add(ligne);
                }
            }
        } catch (IOException e) {
            modele.addAttribute("erreur", "Erreur lors de l'importation du fichier");
        }

        remplirModeleFormulaire(modele, session, typeTransactionId, referenceClient, nomClient, prenomClient, telephoneClient, categorieFournitureId, referenceTransaction, dateTransaction, lignes);
        return "transaction/form";
    }

    private void remplirModeleFormulaire(
        Model modele, HttpSession session,
        Integer typeTransactionId, String referenceClient, String nomClient,
        String prenomClient, String telephoneClient, Integer categorieFournitureId,
        String referenceTransaction, String dateTransaction,
        List<Map<String, Object>> lignes
    ) {
        List<TypeTransaction> typesTransaction = typeTransactionService.getTypesTransaction();
        List<CategorieFourniture> categorieFournitures = categorieFournitureService.getCategories();

        modele.addAttribute("listeTypesTransaction", typesTransaction);
        modele.addAttribute("listeCategoriesFourniture", categorieFournitures);

        if (typeTransactionId == null) {
            typeTransactionId = typesTransaction.isEmpty() ? null : typesTransaction.get(0).getId();
        }

        modele.addAttribute("typeTransactionId", typeTransactionId);
        modele.addAttribute("referenceClient", referenceClient != null ? referenceClient : "");
        modele.addAttribute("nomClient", nomClient != null ? nomClient : "");
        modele.addAttribute("prenomClient", prenomClient != null ? prenomClient : "");
        modele.addAttribute("telephoneClient", telephoneClient != null ? telephoneClient : "");
        modele.addAttribute("categorieFournitureId", categorieFournitureId);
        modele.addAttribute("referenceTransaction", referenceTransaction);
        modele.addAttribute("dateCourante", dateTransaction);
        modele.addAttribute("formLignes", lignes);

        List<Map<String, Object>> fournitures = new ArrayList<>();
        if (categorieFournitureId != null) {
            fournitures = fournitureCatalogService.convertirFournitures(fournitureCatalogService.getFournituresParCategorie(categorieFournitureId));
        }
        modele.addAttribute("formFournitures", fournitures);

        Map<String, Object> formState = new HashMap<>();
        formState.put("typeTransactionId", typeTransactionId);
        formState.put("referenceClient", referenceClient);
        formState.put("nomClient", nomClient);
        formState.put("prenomClient", prenomClient);
        formState.put("telephoneClient", telephoneClient);
        formState.put("categorieFournitureId", categorieFournitureId);
        formState.put("referenceTransaction", referenceTransaction);
        formState.put("dateTransaction", dateTransaction);
        formState.put("lignes", lignes);
        session.setAttribute(FORM_STATE_KEY, formState);
    }


    // // ===== Liste / Detail =====

    @GetMapping("/transactions")
    public String listerTransactions(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) String reference,
        @RequestParam(required = false) String referenceClient,
        @RequestParam(required = false) Integer typeTransactionId,
        @RequestParam(required = false) String dateDebut,
        @RequestParam(required = false) String dateFin,
        Model modele
    ) {
        int taillePage = 10;

        LocalDateTime debut = transactionService.parserDateDebut(dateDebut);
        LocalDateTime fin = transactionService.parserDateFin(dateFin);

        Page<Transaction> pageTransactions = transactionService.rechercherTransactions(
            reference, referenceClient, typeTransactionId, debut, fin, PageRequest.of(page, taillePage)
        );

        List<Map<String, Object>> lignes = new ArrayList<>();
        for (Transaction transaction : pageTransactions.getContent()) {
            Client client = clientService.getClientParId(transaction.getIdClient());
            Map<String, Object> ligne = new HashMap<>();
            ligne.put("idTransaction", transaction.getId());
            ligne.put("referenceTransaction", transaction.getReference());
            ligne.put("referenceClient", client != null ? client.getReference() : "");
            ligne.put("dateTransaction", transactionService.formaterDate(transaction.getDate()));
            ligne.put("quantiteTotale", transactionService.getDetailsParTransaction(transaction.getId())
                .stream().mapToInt(d -> d.getQuantite()).sum());
            ligne.put("total", transaction.getMontantTotal());
            lignes.add(ligne);
        }

        modele.addAttribute("quantiteTotaleVendue", transactionService.getQuantiteTotaleVendue());
        modele.addAttribute("recetteTotale", transactionService.getRecetteTotale());
        modele.addAttribute("listeTransactions", lignes);
        modele.addAttribute("pageActuelle", page);
        modele.addAttribute("totalPages", pageTransactions.getTotalPages());
        modele.addAttribute("reference", reference);
        modele.addAttribute("referenceClient", referenceClient);
        modele.addAttribute("typeTransactionId", typeTransactionId);
        modele.addAttribute("dateDebut", dateDebut);
        modele.addAttribute("dateFin", dateFin);
        modele.addAttribute("listeTypesTransaction", typeTransactionService.convertirTypesTransaction(fournitureCatalogService.getTypesTransaction()));

        return "transaction/liste-transactions";
    }

    @GetMapping("/transactions/{idTransaction}")
    public String afficherDetailTransaction(@PathVariable int idTransaction, Model modele) {
        Transaction transaction = transactionService.getTransactionParId(idTransaction);
        if (transaction == null) {
            return "redirect:/transactions";
        }
        Client client = clientService.getClientParId(transaction.getIdClient());
        TypeTransaction typeTransaction = transactionService.getTypeParId(transaction.getIdType());
        List<DetailTransaction> details = transactionService.getDetailsParTransaction(idTransaction);
        List<HistoriqueTransaction> historiques = transactionService.getHistoriqueParTransaction(idTransaction);

        Map<String, Object> transactionMap = new HashMap<>();
        transactionMap.put("idTransaction", transaction.getId());
        transactionMap.put("referenceTransaction", transaction.getReference());
        transactionMap.put("dateTransaction", transactionService.formaterDate(transaction.getDate()));
        transactionMap.put("montantTotal", transaction.getMontantTotal());

        Map<String, Object> clientMap = new HashMap<>();
        clientMap.put("referenceClient", client != null ? client.getReference() : "");
        clientMap.put("nom", client != null ? client.getNom() : "");
        clientMap.put("prenom", client != null ? client.getPrenom() : "");
        clientMap.put("telephone", client != null ? client.getTelephone() : "");
        transactionMap.put("client", clientMap);

        Map<String, Object> typeMap = new HashMap<>();
        typeMap.put("libelleTypeTransaction", typeTransaction != null ? typeTransaction.getLibelle() : "");
        transactionMap.put("typeTransaction", typeMap);

        List<Map<String, Object>> lignes = new ArrayList<>();
        for (DetailTransaction detail : details) {
            Fourniture fourniture = fournitureCatalogService.trouverFournitureParId(detail.getIdFourniture());
            Map<String, Object> ligne = new HashMap<>();
            ligne.put("referenceFourniture", fourniture != null ? fourniture.getReference() : "");
            ligne.put("quantite", detail.getQuantite());
            ligne.put("prixUnitaire", fourniture != null ? fourniture.getPrixUnitaire() : 0.0);
            ligne.put("montantLigne", detail.getMontantLigne());
            lignes.add(ligne);
        }

        List<Map<String, Object>> historique = new ArrayList<>();
        for (HistoriqueTransaction elementHistorique : historiques) {
            StatutTransaction statut = transactionService.getStatutParId(elementHistorique.getIdStatut());
            Map<String, Object> ligneHistorique = new HashMap<>();
            ligneHistorique.put("statut", statut != null ? statut.getLibelle() : "");
            ligneHistorique.put("sigle", statut != null ? statut.getSigle() : "");
            ligneHistorique.put("date", transactionService.formaterDate(elementHistorique.getDate()));
            historique.add(ligneHistorique);
        }

        modele.addAttribute("transaction", transactionMap);
        modele.addAttribute("lignes", lignes);
        modele.addAttribute("historique", historique);
        return "transaction/detail-transaction";
    }

    // ===== Facture / Confirmer / Annuler =====

    @PostMapping("/transaction/facture")
    public String afficherFacture(
        @RequestParam(value = "typeTransactionId", required = false) Integer typeTransactionId,
        @RequestParam(value = "referenceClient", required = false) String referenceClient,
        @RequestParam(value = "nomClient", required = false) String nomClient,
        @RequestParam(value = "prenomClient", required = false) String prenomClient,
        @RequestParam(value = "telephoneClient", required = false) String telephoneClient,
        @RequestParam(value = "categorieFournitureId", required = false) Integer categorieFournitureId,
        @RequestParam(value = "idFourniture", required = false) List<String> idFournitureStrs,
        @RequestParam(value = "quantite", required = false) List<Integer> quantites,
        @RequestParam(value = "dateTransaction", required = false) String dateTransaction,
        Model modele,
        HttpSession session
    ) {
        List<Integer> idFournitures = transactionService.parserListeEntiers(idFournitureStrs);

        if (transactionService.validerTransaction(typeTransactionId, referenceClient, nomClient, prenomClient, telephoneClient, categorieFournitureId, idFournitures, quantites, dateTransaction)) {
            List<Map<String, Object>> lignes = transactionService.reconstruireLignes(
                idFournitures,
                quantites
            );
            if (lignes.isEmpty()) {
                Map<String, Object> ligneVide = new HashMap<>();
                ligneVide.put("idFourniture", null);
                ligneVide.put("quantite", 1);
                lignes.add(ligneVide);
            }
            remplirModeleFormulaire(
                modele, session, typeTransactionId, referenceClient, nomClient,
                prenomClient, telephoneClient, categorieFournitureId,
                transactionService.genererReferenceTransaction(), dateTransaction, lignes
            );
            modele.addAttribute(
                "erreur",
                "Sélectionnez au moins une fourniture et remplissez les champs obligatoires."
            );
            return "transaction/form";
        }

        TypeTransaction transactionType = fournitureCatalogService.trouverTypeTransaction(typeTransactionId);
        CategorieFourniture categorieFourniture = fournitureCatalogService.trouverCategorie(categorieFournitureId);
        List<Fourniture> selectedFournitures = fournitureCatalogService.trouverFournituresSelectionnees(categorieFournitureId, idFournitures);
        List<Map<String, Object>> lignes = transactionService.construireLignesFacture(selectedFournitures, quantites);

        double totalGeneral = lignes.stream()
            .mapToDouble(l -> (double) l.get("montantLigne"))
            .sum();
        totalGeneral = Math.round(totalGeneral * 100.0) / 100.0;

        modele.addAttribute("typeTransaction", typeTransactionService.convertirTypeTransaction(transactionType));
        modele.addAttribute("categorieFourniture", fournitureCatalogService.convertirCategorie(categorieFourniture));
        modele.addAttribute("referenceClient", referenceClient);
        modele.addAttribute("nomClient", nomClient);
        modele.addAttribute("prenomClient", prenomClient);
        modele.addAttribute("telephoneClient", telephoneClient);
        modele.addAttribute("referenceTransaction", transactionService.genererReferenceTransaction());
        modele.addAttribute("dateTransaction", dateTransaction);
        modele.addAttribute("lignes", lignes);
        modele.addAttribute("totalGeneral", totalGeneral);
        modele.addAttribute("typeTransactionId", typeTransactionId);
        modele.addAttribute("categorieFournitureId", categorieFournitureId);
        modele.addAttribute("idFournitures", idFournitures);
        modele.addAttribute("quantites", quantites);
        return "transaction/facture";
    }

    @PostMapping("/transaction/confirmer")
    public String confirmerTransaction(
        @RequestParam(value = "typeTransactionId", required = false) Integer typeTransactionId,
        @RequestParam(value = "referenceClient", required = false) String referenceClient,
        @RequestParam(value = "nomClient", required = false) String nomClient,
        @RequestParam(value = "prenomClient", required = false) String prenomClient,
        @RequestParam(value = "telephoneClient", required = false) String telephoneClient,
        @RequestParam(value = "categorieFournitureId", required = false) Integer categorieFournitureId,
        @RequestParam(value = "referenceTransaction", required = false) String referenceTransaction,
        @RequestParam(value = "dateTransaction", required = false) String dateTransaction,
        @RequestParam(value = "idFourniture", required = false) List<Integer> idFournitures,
        @RequestParam(value = "quantite", required = false) List<Integer> quantites,
        RedirectAttributes redirectAttributes
    ) {
        if (transactionService.validerTransaction(typeTransactionId, referenceClient, nomClient, prenomClient, telephoneClient, categorieFournitureId, idFournitures, quantites, dateTransaction) || referenceTransaction == null || referenceTransaction.isBlank()) {
            redirectAttributes.addFlashAttribute("erreur", "Veuillez ajouter au moins une ligne et remplir les champs obligatoires");
            return "redirect:/transaction/form";
        }

        transactionService.enregistrerTransaction(
            referenceClient, nomClient, prenomClient, telephoneClient,
            typeTransactionId, referenceTransaction,
            LocalDateTime.parse(dateTransaction),
            categorieFournitureId, idFournitures, quantites
        );

        return "redirect:/transactions";
    }

    @PostMapping("/transaction/annuler")
    public String annulerTransaction() {
        return "redirect:/transaction/form";
    }

    // ===== Export =====

    @PostMapping("/transactions/exporter")
    public void exporterListeTransactions(
        @RequestParam("format") String format,
        @RequestParam(value = "page", defaultValue = "0") int page,
        HttpServletResponse response
    ) throws IOException {
        int taillePage = 10;
        Page<Transaction> pageTransactions = transactionService.rechercherTransactions(null, null, null, null, null, PageRequest.of(page, taillePage));
        List<Transaction> transactions = pageTransactions.getContent();

        if ("csv".equalsIgnoreCase(format)) {
            exporterTransactionCsv(transactions, page, response);
        } else if ("excel".equalsIgnoreCase(format)) {
            exporterTransactionExcel(transactions, page, response);
        } else if ("pdf".equalsIgnoreCase(format)) {
            exporterTransactionPdf(transactions, page, response);
        }
    }

    @PostMapping("/transaction/exporter")
    public void exporterFacture(
        @RequestParam("format") String format,
        @RequestParam("referenceTransaction") String referenceTransaction,
        @RequestParam("dateTransaction") String dateTransaction,
        @RequestParam("referenceClient") String referenceClient,
        @RequestParam("nomClient") String nomClient,
        @RequestParam("prenomClient") String prenomClient,
        @RequestParam("telephoneClient") String telephoneClient,
        @RequestParam("idFourniture") List<Integer> idFournitures,
        @RequestParam("quantite") List<Integer> quantites,
        HttpServletResponse response
    ) throws IOException {
        if ("csv".equalsIgnoreCase(format)) {
            exporterFactureCsv(referenceTransaction, dateTransaction, referenceClient, nomClient, prenomClient, telephoneClient, idFournitures, quantites, response);
        } else if ("pdf".equalsIgnoreCase(format)) {
            exporterFacturePdf(referenceTransaction, dateTransaction, referenceClient, nomClient, prenomClient, telephoneClient, idFournitures, quantites, response);
        } else if ("excel".equalsIgnoreCase(format)) {
            exporterFactureExcel(referenceTransaction, dateTransaction, referenceClient, nomClient, prenomClient, telephoneClient, idFournitures, quantites, response);
        }
    }

    // ===== Prive : export CSV =====

    private void exporterTransactionCsv(List<Transaction> transactions, int page, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"transactions_page_" + (page + 1) + ".csv\"");

        PrintWriter writer = response.getWriter();
        writer.write('\ufeff');
        writer.println("SUIVI DES VENTES - PAGE " + (page + 1));
        writer.println();
        writer.println("Reference;Reference Client;Date;Montant Total");

        for (Transaction t : transactions) {
            Client client = clientService.getClientParId(t.getIdClient());
            writer.println(
                t.getReference() + ";" +
                (client != null ? client.getReference() : "") + ";" +
                transactionService.formaterDate(t.getDate()) + ";" +
                t.getMontantTotal()
            );
        }
        writer.flush();
    }

    private void exporterFactureCsv(String refTransaction, String date, String refClient, String nom, String prenom, String tel, List<Integer> ids, List<Integer> qtes, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"facture_" + refTransaction + ".csv\"");

        PrintWriter writer = response.getWriter();
        writer.write('\ufeff');
        writer.println("RECAPITULATIF DE TRANSACTION");
        writer.println("Reference Transaction;" + refTransaction);
        writer.println("Date;" + date);
        writer.println();
        writer.println("INFORMATIONS DU CLIENT");
        writer.println("Reference Client;" + refClient);
        writer.println("Nom;" + nom);
        writer.println("Prenom;" + prenom);
        writer.println("Telephone;" + tel);
        writer.println();
        writer.println("DETAILS DE LA FACTURE");
        writer.println("ID Fourniture;Quantite");

        int totalArticles = 0;
        for (int i = 0; i < ids.size(); i++) {
            Integer qte = qtes.get(i);
            writer.println(ids.get(i) + ";" + qte);
            totalArticles += (qte != null) ? qte : 0;
        }
        writer.println();
        writer.println("Quantite Totale Articles Vendu(s);" + totalArticles);
        writer.flush();
    }

    // ===== Prive : export Excel =====

    private void exporterTransactionExcel(List<Transaction> transactions, int page, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"transactions_page_" + (page + 1) + ".xlsx\"");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Page " + (page + 1));
            int idx = 0;

            sheet.createRow(idx++).createCell(0).setCellValue("SUIVI DES VENTES - PAGE " + (page + 1));
            idx++;

            Row entete = sheet.createRow(idx++);
            entete.createCell(0).setCellValue("Reference");
            entete.createCell(1).setCellValue("Reference Client");
            entete.createCell(2).setCellValue("Date");
            entete.createCell(3).setCellValue("Montant Total");

            for (Transaction t : transactions) {
                Client client = clientService.getClientParId(t.getIdClient());
                Row ligneData = sheet.createRow(idx++);
                ligneData.createCell(0).setCellValue(t.getReference());
                ligneData.createCell(1).setCellValue(client != null ? client.getReference() : "");
                ligneData.createCell(2).setCellValue(transactionService.formaterDate(t.getDate()));
                ligneData.createCell(3).setCellValue(t.getMontantTotal());
            }
            workbook.write(response.getOutputStream());
        }
    }

    private void exporterFactureExcel(String refTransaction, String date, String refClient, String nom, String prenom, String tel, List<Integer> ids, List<Integer> qtes, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"facture_" + refTransaction + ".xlsx\"");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Facture");
            int idx = 0;

            sheet.createRow(idx++).createCell(0).setCellValue("FACTURATION");
            sheet.createRow(idx++).createCell(0).setCellValue("Reference : " + refTransaction);
            sheet.createRow(idx++).createCell(0).setCellValue("Date : " + date);
            sheet.createRow(idx++).createCell(0).setCellValue("Client : " + nom + " " + prenom);
            idx++;

            Row entete = sheet.createRow(idx++);
            entete.createCell(0).setCellValue("ID Fourniture");
            entete.createCell(1).setCellValue("Quantite");

            int totalArticles = 0;
            for (int i = 0; i < ids.size(); i++) {
                Row ligneData = sheet.createRow(idx++);
                ligneData.createCell(0).setCellValue(ids.get(i));
                int qte = qtes.get(i) != null ? qtes.get(i) : 0;
                ligneData.createCell(1).setCellValue(qte);
                totalArticles += qte;
            }
            idx++;

            Row ligneTotal = sheet.createRow(idx);
            ligneTotal.createCell(0).setCellValue("Total Articles :");
            ligneTotal.createCell(1).setCellValue(totalArticles);

            workbook.write(response.getOutputStream());
        }
    }

    // ===== Prive : export PDF =====

    private void exporterTransactionPdf(List<Transaction> transactions, int page, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"transactions_page_" + (page + 1) + ".pdf\"");

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            document.add(new Paragraph("SUIVI DES VENTES - PAGE " + (page + 1)));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.addCell("Reference");
            table.addCell("Reference Client");
            table.addCell("Date");
            table.addCell("Montant Total");

            for (Transaction t : transactions) {
                Client client = clientService.getClientParId(t.getIdClient());
                table.addCell(t.getReference());
                table.addCell(client != null ? client.getReference() : "");
                table.addCell(transactionService.formaterDate(t.getDate()));
                table.addCell(String.valueOf(t.getMontantTotal()));
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new IOException("Erreur generation PDF", e);
        }
    }

    private void exporterFacturePdf(String refTransaction, String date, String refClient, String nom, String prenom, String tel, List<Integer> ids, List<Integer> qtes, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"facture_" + refTransaction + ".pdf\"");

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            document.add(new Paragraph("FACTURATION"));
            document.add(new Paragraph("Reference : " + refTransaction));
            document.add(new Paragraph("Date : " + date));
            document.add(new Paragraph("Client : " + nom + " " + prenom));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.addCell("Designation Fourniture");
            table.addCell("Quantite");

            int totalArticles = 0;
            for (int i = 0; i < ids.size(); i++) {
                Integer idFourniture = ids.get(i);
                String nomFourniture = "Fourniture #" + idFourniture;
                if (idFourniture != null) {
                    Fourniture f = fournitureCatalogService.trouverFournitureParId(idFourniture);
                    if (f != null) {
                        nomFourniture = f.getReference();
                    }
                }
                table.addCell(nomFourniture);
                int qte = qtes.get(i) != null ? qtes.get(i) : 0;
                table.addCell(String.valueOf(qte));
                totalArticles += qte;
            }
            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total Articles : " + totalArticles));
            document.close();
        } catch (Exception e) {
            throw new IOException("Erreur generation PDF", e);
        }
    }

    // ===== Prive : lecture fichier =====

    private List<String[]> lireCsv(MultipartFile fichier) throws IOException {
        List<String[]> lignes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fichier.getInputStream(), StandardCharsets.UTF_8))) {
            String ligne;
            boolean premiere = true;
            while ((ligne = reader.readLine()) != null) {
                if (premiere) {
                    if (ligne.length() > 0 && ligne.charAt(0) == '\uFEFF') {
                        ligne = ligne.substring(1);
                    }
                    premiere = false;
                }
                if (ligne.trim().isEmpty()) continue;
                String separateur = ligne.contains(";") ? ";" : ",";
                lignes.add(ligne.split(separateur, -1));
            }
        }
        return lignes;
    }

    private List<String[]> lireExcel(MultipartFile fichier) throws IOException {
        List<String[]> lignes = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(fichier.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (org.apache.poi.ss.usermodel.Row row : sheet) {
                List<String> champs = new ArrayList<>();
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    org.apache.poi.ss.usermodel.Cell cell = row.getCell(i);
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING -> champs.add(cell.getStringCellValue());
                            case NUMERIC -> champs.add(String.valueOf((long) cell.getNumericCellValue()));
                            default -> champs.add("");
                        }
                    } else {
                        champs.add("");
                    }
                }
                lignes.add(champs.toArray(new String[0]));
            }
        }
        return lignes;
    }
}
