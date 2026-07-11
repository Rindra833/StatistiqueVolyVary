package com.volyVary.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.volyVary.model.Client;
import com.volyVary.repository.ClientRepository;

@RestController
@RequestMapping("/api/clients")
/**
 * API REST historique des clients. Elle est conservée pour les modules non encore migrés vers JSP.
 */
public class ClientController {

    private final ClientRepository clientRepository;

    /**
     * Injecte l'accès aux données clients utilisé par toutes les opérations CRUD.
     */
    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping
    /**
     * Retourne la liste complète des clients sous forme JSON.
     */
    public List<Client> lister() {
        return clientRepository.findAll();
    }

    @GetMapping("/{id}")
    /**
     * Retourne le client demandé ou une réponse HTTP 404 lorsque son identifiant n'existe pas.
     */
    public ResponseEntity<Client> obtenir(@PathVariable Integer id) {
        return ResponseEntity.of(clientRepository.findById(id));
    }

    @PostMapping
    /**
     * Force un nouvel identifiant puis persiste le client reçu avec le statut HTTP 201 Created.
     */
    public ResponseEntity<Client> creer(@RequestBody Client client) {
        client.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(clientRepository.save(client));
    }

    @PutMapping("/{id}")
    /**
     * Remplace les données du client existant. L'identifiant de l'URL reste prioritaire sur toute
     * valeur éventuellement envoyée dans le corps JSON.
     */
    public ResponseEntity<Client> modifier(
        @PathVariable Integer id,
        @RequestBody Client client
    ) {
        if (!clientRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        client.setId(id);
        return ResponseEntity.ok(clientRepository.save(client));
    }

    @DeleteMapping("/{id}")
    /**
     * Supprime le client s'il existe et retourne 204 No Content ; sinon retourne 404.
     */
    public ResponseEntity<Void> supprimer(@PathVariable Integer id) {
        if (!clientRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        clientRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
