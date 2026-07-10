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

import com.volyVary.model.Fourniture;
import com.volyVary.repository.FournitureRepository;

@RestController
@RequestMapping("/api/fournitures")
public class FournitureController {

    private final FournitureRepository fournitureRepository;

    public FournitureController(FournitureRepository fournitureRepository) {
        this.fournitureRepository = fournitureRepository;
    }

    @GetMapping
    public List<Fourniture> lister() {
        return fournitureRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fourniture> obtenir(@PathVariable Long id) {
        return ResponseEntity.of(fournitureRepository.findById(id));
    }

    @PostMapping
    public ResponseEntity<Fourniture> creer(@RequestBody Fourniture fourniture) {
        fourniture.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(fournitureRepository.save(fourniture));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fourniture> modifier(
        @PathVariable Long id,
        @RequestBody Fourniture fourniture
    ) {
        if (!fournitureRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        fourniture.setId(id);
        return ResponseEntity.ok(fournitureRepository.save(fourniture));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        if (!fournitureRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        fournitureRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
