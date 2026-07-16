package com.volyVary.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.volyVary.modele.*;
import com.volyVary.repository.*;

@Component
public class InitialAdminConfig implements ApplicationRunner {

    private UtilisateurRepository utilisateurRepository;
    private EmployeeRepository employeeRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private String nom;
    private String motDePasse;
    private String role;

    public InitialAdminConfig(
        UtilisateurRepository utilisateurRepository,
        EmployeeRepository employeeRepository,
        BCryptPasswordEncoder passwordEncoder,
        @Value("${app.bootstrap-admin.nom:}") String nom,
        @Value("${app.bootstrap-admin.password:}") String motDePasse,
        @Value("${app.bootstrap-admin.role:Administrateur}") String role
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.nom = nom;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (nom.isBlank() || motDePasse.isBlank() || utilisateurRepository.findByNom(nom).isPresent()) {
            return;
        }
        Employee employee = employeeRepository.findById(1).orElse(null);

        Utilisateur administrateur = new Utilisateur();
        administrateur.setNom(nom);
        administrateur.setMdp(passwordEncoder.encode(motDePasse));
        administrateur.setRole(role);
        administrateur.setEmployee(employee);
        utilisateurRepository.save(administrateur);
    }
}
