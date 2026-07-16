package com.volyVary.dto;

import com.volyVary.modele.Distribution;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FactureResume {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Integer id;
    private final String reference;
    private final LocalDate date;
    private final String dateFormatee;
    private final String clientNom;
    private final String clientPrenom;
    private final String clientReference;
    private final String lieu;
    private final String livreur;
    private final double quantite;
    private final double montant;
    private final String statut;

    public FactureResume(Distribution distribution, double quantite, double montant, String statut) {
        this.id = distribution.getId();
        this.reference = distribution.getReference();
        this.date = distribution.getDate();
        this.dateFormatee = this.date != null ? this.date.format(FORMAT_DATE) : "-";
        this.clientNom = distribution.getClient() != null ? distribution.getClient().getNom() : "";
        this.clientPrenom = distribution.getClient() != null ? distribution.getClient().getPrenom() : "";
        this.clientReference = distribution.getClient() != null ? distribution.getClient().getReference() : "";
        this.lieu = distribution.getLieu() != null ? distribution.getLieu().getNom() : "";
        this.livreur = distribution.getLivreur() != null ? distribution.getLivreur().getMatriculeVehicule() : "";
        this.quantite = quantite;
        this.montant = montant;
        this.statut = statut;
    }

    public Integer getId() { return id; }
    public String getReference() { return reference; }
    public LocalDate getDate() { return date; }
    public String getDateFormatee() { return dateFormatee; }
    public String getClientNom() { return clientNom; }
    public String getClientPrenom() { return clientPrenom; }
    public String getClientReference() { return clientReference; }
    public String getLieu() { return lieu; }
    public String getLivreur() { return livreur; }
    public double getQuantite() { return quantite; }
    public double getMontant() { return montant; }
    public String getStatut() { return statut; }
}

