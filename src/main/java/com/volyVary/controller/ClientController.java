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
public class ClientController {

    private final ClientRepository clientRepository;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping
    public List<Client> lister() {
        return clientRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> obtenir(@PathVariable Integer id) {
        return ResponseEntity.of(clientRepository.findById(id));
    }

    @PostMapping
    public ResponseEntity<Client> creer(@RequestBody Client client) {
        client.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(clientRepository.save(client));
    }

    @PutMapping("/{id}")
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
    public ResponseEntity<Void> supprimer(@PathVariable Integer id) {
        if (!clientRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        clientRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
