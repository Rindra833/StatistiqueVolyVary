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

import com.volyVary.model.Livreur;
import com.volyVary.repository.LivreurRepository;

@RestController
@RequestMapping("/api/livreurs")
public class LivreurController {

    private final LivreurRepository livreurRepository;

    public LivreurController(LivreurRepository livreurRepository) {
        this.livreurRepository = livreurRepository;
    }

    @GetMapping
    public List<Livreur> lister() {
        return livreurRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livreur> obtenir(@PathVariable Long id) {
        return ResponseEntity.of(livreurRepository.findById(id));
    }

    @PostMapping
    public ResponseEntity<Livreur> creer(@RequestBody Livreur livreur) {
        livreur.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(livreurRepository.save(livreur));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Livreur> modifier(
        @PathVariable Long id,
        @RequestBody Livreur livreur
    ) {
        if (!livreurRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        livreur.setId(id);
        return ResponseEntity.ok(livreurRepository.save(livreur));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        if (!livreurRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        livreurRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
