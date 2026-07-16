package com.volyVary.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.volyVary.modele.*;
import com.volyVary.repository.*;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ClientRepository clientRepository;

    

    @Autowired
    private TransactionDetailRepository transactionDetailRepository;

    @Autowired
    private HistoriqueTransactionRepository historiqueTransactionRepository;

    @Autowired
    private StatutTransactionRepository statutTransactionRepository;

    @Autowired
    private TypeTransactionRepository typeTransactionRepository;

    @Autowired
    private FournitureRepository fournitureRepository;

    public String genererReferenceTransaction() {
        long nextId = transactionRepository.count() + 1;

        return String.format("TR%04d", nextId);
    }

    public Transaction getDernierTransaction(){
        return transactionRepository.findFirstByOrderByIdDesc();
    }

    public List<Map<String, Object>> reconstruireLignes(List<Integer> idFournitures, List<Integer> quantites) {
        List<Map<String, Object>> lignes = new ArrayList<>();
        if (idFournitures != null && quantites != null) {
            for (int i = 0; i < idFournitures.size(); i++) {
                Map<String, Object> ligne = new HashMap<>();
                ligne.put("idFourniture", idFournitures.get(i));
                ligne.put("quantite", i < quantites.size() ? quantites.get(i) : 1);
                lignes.add(ligne);
            }
        }
        return lignes;
    }

    public List<Transaction> getTransactions() {
        return transactionRepository.findAllByOrderByDateDesc();
    }

    public int getQuantiteTotaleVendue() {
        return transactionRepository.getTotalQuantitySold();
    }

    public double getRecetteTotale() {
        return transactionRepository.getTotalRevenue();
    }

    public Transaction getTransactionParId(int idTransaction) {
        return transactionRepository.findById(idTransaction).orElse(null);
    }

    public List<DetailTransaction> getDetailsParTransaction(int idTransaction) {
        return transactionDetailRepository.findAllByIdTransaction(idTransaction);
    }

    public List<HistoriqueTransaction> getHistoriqueParTransaction(int idTransaction) {
        return historiqueTransactionRepository.findAllByIdTransactionOrderByDateAsc(idTransaction);
    }

    public Page<Transaction> rechercherTransactions(String reference, String referenceClient, Integer typeTransactionId, LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable) {
        String ref = (reference == null || reference.isBlank()) ? null : reference;
        String refClient = (referenceClient == null || referenceClient.isBlank()) ? null : referenceClient;
        return transactionRepository.searchTransactions(ref, refClient, typeTransactionId, dateDebut, dateFin, pageable);
    }

    public String formaterDate(LocalDateTime dateHeure) {
        return dateHeure == null ? "" : dateHeure.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public double calculerMontantLigne(Fourniture fourniture, int quantite) {
        return Math.round(fourniture.getPrixUnitaire() * quantite * 100.0) / 100.0;
    }

    public List<Map<String, Object>> construireLignesFacture(List<Fourniture> fournitures, List<Integer> quantites) {
        List<Map<String, Object>> lignes = new ArrayList<>();
        for (int index = 0; index < fournitures.size(); index++) {
            Fourniture fourniture = fournitures.get(index);
            int quantite = quantites.get(index);
            double montantLigne = calculerMontantLigne(fourniture, quantite);

            Map<String, Object> ligne = new HashMap<>();
            ligne.put("referenceFourniture", fourniture.getReference());
            ligne.put("quantite", quantite);
            ligne.put("prixUnitaire", fourniture.getPrixUnitaire());
            ligne.put("montantLigne", montantLigne);
            lignes.add(ligne);
        }
        return lignes;
    }

    public boolean validerTransaction(Integer typeTransactionId, String referenceClient, String nomClient,
            String prenomClient, String telephoneClient, Integer categorieFournitureId,
            List<Integer> idFournitures, List<Integer> quantites, String dateTransaction) {
        return typeTransactionId == null
            || categorieFournitureId == null
            || referenceClient == null || referenceClient.isBlank()
            || nomClient == null || nomClient.isBlank()
            || prenomClient == null || prenomClient.isBlank()
            || telephoneClient == null || telephoneClient.isBlank()
            || dateTransaction == null || dateTransaction.isBlank()
            || idFournitures == null || idFournitures.isEmpty()
            || quantites == null || quantites.isEmpty()
            || idFournitures.size() != quantites.size();
    }

    public List<Integer> parserListeEntiers(List<String> chaines) {
        List<Integer> resultat = new ArrayList<>();
        if (chaines != null) {
            for (String s : chaines) {
                if (s != null && !s.isBlank()) {
                    try {
                        resultat.add(Integer.parseInt(s.trim()));
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
        }
        return resultat;
    }

    public LocalDateTime parserDateDebut(String dateDebut) {
        try {
            if (dateDebut != null && !dateDebut.isBlank()) {
                return LocalDateTime.parse(dateDebut + "T00:00:00");
            }
        } catch (Exception e) { }
        return null;
    }

    public LocalDateTime parserDateFin(String dateFin) {
        try {
            if (dateFin != null && !dateFin.isBlank()) {
                return LocalDateTime.parse(dateFin + "T23:59:59");
            }
        } catch (Exception e) { }
        return null;
    }

    public TypeTransaction getTypeParId(int idType) {
        return typeTransactionRepository.findById(idType).orElse(null);
    }

    public StatutTransaction getStatutParId(int idStatut) {
        return statutTransactionRepository.findById(idStatut).orElse(null);
    }

    @Transactional
    public Transaction enregistrerTransaction(String referenceClient, String nomClient, String prenomClient,
            String telephoneClient, int typeTransactionId, String referenceTransaction,
            LocalDateTime dateTransaction, int categorieFournitureId, List<Integer> idFournitures,
            List<Integer> quantites) {

        Client client = clientRepository.findByReferenceContainingIgnoreCase(referenceClient);

        if(client == null){
            client = new Client();
        }

        client.setReference(referenceClient);
        client.setNom(nomClient);
        client.setPrenom(prenomClient);
        client.setTelephone(telephoneClient);
        client = clientRepository.save(client);

        Transaction transaction = new Transaction();
        transaction.setIdClient(client.getId());
        transaction.setIdType(typeTransactionId);
        transaction.setReference(referenceTransaction);
        transaction.setDate(dateTransaction);
        transaction.setMontantTotal(0.0);

        Transaction transactionEnregistree = transactionRepository.save(transaction);

        double totalGeneral = 0.0;
        List<DetailTransaction> listDetails = new ArrayList<>();

        for (int index = 0; index < idFournitures.size(); index++) {
            int idFourniture = idFournitures.get(index);
            int quantite = quantites.get(index);

            Fourniture fourniture = fournitureRepository.findById(idFourniture).orElse(null);
            double prixUnitaire = fourniture != null ? fourniture.getPrixUnitaire() : 0.0;
            double montantLigne = Math.round(prixUnitaire * quantite * 100.0) / 100.0;

            DetailTransaction transactionDetail = new DetailTransaction();
            transactionDetail.setIdTransaction(transactionEnregistree.getId());
            transactionDetail.setIdFourniture(idFourniture);
            transactionDetail.setQuantite(quantite);
            transactionDetail.setMontantLigne(montantLigne);
            listDetails.add(transactionDetail);

            totalGeneral += montantLigne;
        }

        transactionDetailRepository.saveAll(listDetails);

        transactionEnregistree.setMontantTotal(Math.round(totalGeneral * 100.0) / 100.0);
        transactionRepository.save(transactionEnregistree);

        StatutTransaction statutInitial = getStatutInitial(typeTransactionId);

        HistoriqueTransaction historique = new HistoriqueTransaction();
        historique.setIdTransaction(transactionEnregistree.getId());
        historique.setIdStatut(statutInitial.getId());
        historique.setDate(LocalDateTime.now());

        historiqueTransactionRepository.save(historique);

        return transactionEnregistree;
    }

    private StatutTransaction getStatutInitial(int typeTransactionId) {
        StatutTransaction statut = statutTransactionRepository.findBySigle("ATT").orElse(null);
        if (statut != null) return statut;

        statut = statutTransactionRepository.findBySigle("VAL").orElse(null);
        if (statut != null) return statut;

        TypeTransaction typeTransaction = typeTransactionRepository.findById(typeTransactionId).orElse(null);
        boolean estCredit = typeTransaction != null && typeTransaction.getLibelle() != null
            && typeTransaction.getLibelle().toLowerCase().contains("crédit");

        StatutTransaction nouveauStatut = new StatutTransaction();
        nouveauStatut.setLibelle(estCredit ? "En attente" : "Validée");
        nouveauStatut.setSigle(estCredit ? "ATT" : "VAL");
        return statutTransactionRepository.save(nouveauStatut);
    }
}
