package com.volyVary.service;

import com.volyVary.repository.*;
import com.volyVary.modele.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProduitRepository produitRepository;

    public Client getClientParReference(String reference) {
        if (reference == null || reference.isBlank()) {
            return null;
        }
        return clientRepository.findFirstByReferenceIgnoreCase(reference.trim()).orElse(null);
    }

    public Client getClientParId(int id) {
        return clientRepository.findById(id).orElse(null);
    }

    public Client enregistrerClient(Client client) {
        if (client.getDate() == null) {
            client.setDate(LocalDate.now());
        }
        return clientRepository.save(client);
    }

    public String obtenirDerniereReference() {
        List<Client> clients = clientRepository.trouverClientsParIdDesc();
        int prochainId = clients.isEmpty() ? 1 : clients.get(0).getId() + 1;
        return String.format("CL%04d", prochainId);
    }


    public Client trouverOuCreerClient(String reference, String nom, String prenom, String telephone, String dateHeure){
        Client clientExistant = getClientParReference(reference);
        if (clientExistant != null) {
            verifierCoherenceClient(clientExistant, nom, prenom, telephone);
            return clientExistant;
        }
        
        validerTelephone(telephone);
        if (!clientRepository.TrouverParTelephone(telephone).isEmpty()) {
            throw new IllegalArgumentException("Numero de telephone existe deja");
        }
        
        Client nouveauClient = new Client();
        nouveauClient.setNom(nom);
        nouveauClient.setPrenom(prenom);
        nouveauClient.setTelephone(telephone);
        nouveauClient.setDate(LocalDateTime.parse(dateHeure).toLocalDate());
        nouveauClient.setReference(reference);
        return clientRepository.save(nouveauClient); 
    }

    private void validerTelephone(String telephone) {
        if (telephone == null || telephone.isBlank()) {
            throw new IllegalArgumentException("Le numéro de téléphone est obligatoire");
        }
        if (!telephone.matches("^[0-9 ]+$")) {
            throw new IllegalArgumentException("Le numéro de téléphone ne doit contenir que des chiffres : " + telephone);
        }
    }

    private void verifierCoherenceClient(Client client, String nom, String prenom, String telephone) {
        if (!client.getNom().trim().equalsIgnoreCase(nom.trim())) {
            throw new IllegalArgumentException("Le nom ne correspond pas au client existant : " + client.getReference());
        }

        if (!client.getPrenom().trim().equalsIgnoreCase(prenom.trim())) {
            throw new IllegalArgumentException("Le prenom ne correspond pas au client existant : " + client.getReference());
        }

        if (!client.getTelephone().trim().equals(telephone.trim())) {
            throw new IllegalArgumentException("Le numero de telephone ne correspond pas au client existant : " + client.getReference());
        }
    }

    @Transactional
    public Client sauvegarderClient(Client client) {
        Client clientExistant = getClientParReference(client.getReference());

        if(clientExistant==null){
            clientExistant = new Client();
        }

        clientExistant.setReference(client.getReference());
        clientExistant.setNom(client.getNom());
        clientExistant.setPrenom(client.getPrenom());
        clientExistant.setTelephone(client.getTelephone());
        clientExistant.setDate(LocalDate.now());
        return clientRepository.save(clientExistant);
    }

    @Transactional(readOnly = true)
    public List<Produit> chargerProduits() {
        return produitRepository.findAll();
    }

    /**
     * Liste le répertoire des clients en filtrant sur la référence, le nom, le prénom ou le
     * téléphone. Ce service est partagé par les modules Transaction, Collecte et Distribution.
     */
    @Transactional(readOnly = true)
    public List<Client> listerClients(String recherche) {
        String texte = recherche == null ? "" : recherche.trim().toLowerCase();
        return clientRepository.findAll().stream()
            .filter(client -> texte.isEmpty()
                || normaliser(client.getReference()).contains(texte)
                || normaliser(client.getNom()).contains(texte)
                || normaliser(client.getPrenom()).contains(texte)
                || normaliser(client.getTelephone()).contains(texte))
            .sorted(Comparator.comparing(Client::getReference, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    /**
     * Crée ou modifie un client après avoir vérifié que sa référence n'est pas déjà attribuée à
     * une autre personne. La date du jour est utilisée lorsque le formulaire n'en fournit pas.
     */
    @Transactional
    public Client enregistrerClient(Integer id, Client donnees) {
        validerClient(donnees);
        Client memeReference = getClientParReference(donnees.getReference());
        if (memeReference != null && (id == null || memeReference.getId() != id)) {
            throw new IllegalArgumentException("Cette référence client existe déjà.");
        }

        Client client = id == null
            ? new Client()
            : clientRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Client introuvable : " + id)
            );
        client.setReference(donnees.getReference().trim());
        client.setNom(donnees.getNom().trim());
        client.setPrenom(donnees.getPrenom().trim());
        client.setTelephone(donnees.getTelephone().trim());
        client.setDate(donnees.getDate() == null ? LocalDate.now() : donnees.getDate());
        return clientRepository.save(client);
    }

    /**
     * Supprime uniquement un client qui n'est référencé par aucune opération. La contrainte de clé
     * étrangère PostgreSQL protège l'historique métier si le client est déjà utilisé.
     */
    @Transactional
    public void supprimerClient(Integer id) {
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            clientRepository.flush();
        }
    }

    /** Vérifie les quatre informations indispensables à l'identification d'un client. */
    private void validerClient(Client client) {
        if (client == null || client.getReference() == null || client.getReference().isBlank()
            || client.getNom() == null || client.getNom().isBlank()
            || client.getPrenom() == null || client.getPrenom().isBlank()) {
            throw new IllegalArgumentException("La référence, le nom et le prénom sont obligatoires.");
        }
        validerTelephone(client.getTelephone());
    }

    /** Normalise une valeur nullable pour les recherches textuelles. */
    private String normaliser(String valeur) {
        return valeur == null ? "" : valeur.toLowerCase();
    }
}
