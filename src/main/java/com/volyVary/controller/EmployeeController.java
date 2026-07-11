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

import com.volyVary.model.Employee;
import com.volyVary.repository.EmployeeRepository;

@RestController
@RequestMapping("/api/employees")
/**
 * API REST historique des employés. Elle reste disponible pour les modules RH non migrés vers JSP.
 */
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    /**
     * Injecte l'accès aux données employés utilisé par toutes les opérations CRUD.
     */
    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    /**
     * Retourne tous les employés sous forme JSON.
     */
    public List<Employee> lister() {
        return employeeRepository.findAll();
    }

    @GetMapping("/{id}")
    /**
     * Retourne l'employé demandé ou une réponse HTTP 404 s'il n'existe pas.
     */
    public ResponseEntity<Employee> obtenir(@PathVariable Integer id) {
        return ResponseEntity.of(employeeRepository.findById(id));
    }

    @PostMapping
    /**
     * Crée un nouvel employé en ignorant tout identifiant fourni dans le corps JSON.
     */
    public ResponseEntity<Employee> creer(@RequestBody Employee employee) {
        employee.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(employeeRepository.save(employee));
    }

    @PutMapping("/{id}")
    /**
     * Met à jour un employé existant en utilisant l'identifiant provenant de l'URL.
     */
    public ResponseEntity<Employee> modifier(
        @PathVariable Integer id,
        @RequestBody Employee employee
    ) {
        if (!employeeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        employee.setId(id);
        return ResponseEntity.ok(employeeRepository.save(employee));
    }

    @DeleteMapping("/{id}")
    /**
     * Supprime l'employé existant et retourne 204, ou retourne 404 en cas d'identifiant inconnu.
     */
    public ResponseEntity<Void> supprimer(@PathVariable Integer id) {
        if (!employeeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        employeeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
