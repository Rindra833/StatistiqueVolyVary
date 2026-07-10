package com.volyVary.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.volyVary.dto.UtilisateurRequest;
import com.volyVary.dto.UtilisateurResponse;
import com.volyVary.model.Employee;
import com.volyVary.model.Utilisateur;
import com.volyVary.repository.EmployeeRepository;
import com.volyVary.repository.UtilisateurRepository;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurRepository utilisateurRepository;
    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UtilisateurController(
        UtilisateurRepository utilisateurRepository,
        EmployeeRepository employeeRepository,
        BCryptPasswordEncoder passwordEncoder
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UtilisateurResponse> lister() {
        return utilisateurRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurResponse> obtenir(@PathVariable Long id) {
        return utilisateurRepository.findById(id)
            .map(this::toResponse)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> creer(@RequestBody UtilisateurRequest request) {
        if (request.getMdp() == null || request.getMdp().isBlank()) {
            return ResponseEntity.badRequest()
                .body("Le mot de passe est obligatoire");
        }

        Employee employee = resolveEmployee(request.getEmployeeId());
        if (request.getEmployeeId() != null && employee == null) {
            return ResponseEntity.badRequest()
                .body("L'employee indiqué n'existe pas");
        }

        Utilisateur utilisateur = new Utilisateur();
        appliquer(request, utilisateur, employee);
        utilisateur.setMdp(passwordEncoder.encode(request.getMdp()));

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toResponse(utilisateurRepository.save(utilisateur)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifier(
        @PathVariable Long id,
        @RequestBody UtilisateurRequest request
    ) {
        Utilisateur utilisateur = utilisateurRepository.findById(id).orElse(null);
        if (utilisateur == null) {
            return ResponseEntity.notFound().build();
        }

        Employee employee = resolveEmployee(request.getEmployeeId());
        if (request.getEmployeeId() != null && employee == null) {
            return ResponseEntity.badRequest()
                .body("L'employee indiqué n'existe pas");
        }

        appliquer(request, utilisateur, employee);
        if (request.getMdp() != null && !request.getMdp().isBlank()) {
            utilisateur.setMdp(passwordEncoder.encode(request.getMdp()));
        }

        return ResponseEntity.ok(toResponse(utilisateurRepository.save(utilisateur)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        if (!utilisateurRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        utilisateurRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Employee resolveEmployee(Long employeeId) {
        if (employeeId == null) {
            return null;
        }
        return employeeRepository.findById(employeeId).orElse(null);
    }

    private void appliquer(
        UtilisateurRequest request,
        Utilisateur utilisateur,
        Employee employee
    ) {
        utilisateur.setNom(request.getNom());
        utilisateur.setRole(request.getRole());
        utilisateur.setEmployee(employee);
    }

    private UtilisateurResponse toResponse(Utilisateur utilisateur) {
        Long employeeId = utilisateur.getEmployee() == null
            ? null
            : utilisateur.getEmployee().getId();

        return new UtilisateurResponse(
            utilisateur.getId(),
            utilisateur.getNom(),
            utilisateur.getRole(),
            employeeId
        );
    }
}
