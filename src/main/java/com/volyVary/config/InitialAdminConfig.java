package com.volyVary.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.volyVary.model.Utilisateur;
import com.volyVary.repository.UtilisateurRepository;

@Component
public class InitialAdminConfig implements ApplicationRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final String nom;
    private final String motDePasse;
    private final String role;

    public InitialAdminConfig(
        UtilisateurRepository utilisateurRepository,
        BCryptPasswordEncoder passwordEncoder,
        @Value("${app.bootstrap-admin.nom:}") String nom,
        @Value("${app.bootstrap-admin.password:}") String motDePasse,
        @Value("${app.bootstrap-admin.role:Administrateur}") String role
    ) {
        this.utilisateurRepository = utilisateurRepository;
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

        Utilisateur administrateur = new Utilisateur();
        administrateur.setNom(nom);
        administrateur.setMdp(passwordEncoder.encode(motDePasse));
        administrateur.setRole(role);
        utilisateurRepository.save(administrateur);
    }
}
