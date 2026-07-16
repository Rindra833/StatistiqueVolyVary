package com.volyVary.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.volyVary.modele.*;
import com.volyVary.repository.*;

@Service
public class UtilisateurService {

    private UtilisateurRepository utilisateurRepository;
    private EmployeeRepository employeeRepository;
    private BCryptPasswordEncoder encodeurMotDePasse;

    
    public UtilisateurService(
        UtilisateurRepository utilisateurRepository,
        EmployeeRepository employeeRepository,
        BCryptPasswordEncoder encodeurMotDePasse
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.employeeRepository = employeeRepository;
        this.encodeurMotDePasse = encodeurMotDePasse;
    }

    
    public List<Utilisateur> lister(String recherche, String role) {
        String texteRecherche = normaliser(recherche);
        String roleRecherche = normaliser(role);

        return utilisateurRepository.findAll().stream()
            .filter(utilisateur -> texteRecherche.isEmpty()
                || normaliser(utilisateur.getNom()).contains(texteRecherche))
            .filter(utilisateur -> roleRecherche.isEmpty()
                || normaliser(utilisateur.getRole()).equals(roleRecherche))
            .sorted(Comparator.comparing(Utilisateur::getNom, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    
    public Utilisateur obtenir(Integer id) {
        return utilisateurRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + id));
    }

    
    public List<Employee> listerEmployees() {
        return employeeRepository.findAll().stream()
            .sorted(Comparator.comparing(Employee::getNom, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    
    public List<String> listerRoles() {
        return List.of(
            "Administrateur",
            "Responsable Transaction",
            "Responsable Collecte",
            "Responsable Transformation",
            "Responsable Distribution",
            "Responsable Statistiques"
        );
    }

    
    public void enregistrer(
        Integer id,
        String nom,
        String motDePasse,
        String role,
        Integer employeeId
    ) {
        Utilisateur utilisateur = id == null ? new Utilisateur() : obtenir(id);
        utilisateur.setNom(nom);
        utilisateur.setRole(role);
        utilisateur.setEmployee(resoudreEmployee(employeeId));

        if (id == null && (motDePasse == null || motDePasse.isBlank())) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire pour un nouveau compte.");
        }
        if (motDePasse != null && !motDePasse.isBlank()) {
            utilisateur.setMdp(encodeurMotDePasse.encode(motDePasse));
        }
        utilisateurRepository.save(utilisateur);
    }

    
    public void supprimer(Integer id) {
        if (utilisateurRepository.existsById(id)) {
            utilisateurRepository.deleteById(id);
        }
    }

    
    private Employee resoudreEmployee(Integer employeeId) {
        if (employeeId == null) {
            return null;
        }
        return employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Employé introuvable : " + employeeId));
    }

    private String normaliser(String valeur) {
        return valeur == null ? "" : valeur.trim().toLowerCase();
    }
}
