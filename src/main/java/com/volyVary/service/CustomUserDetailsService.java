package com.volyVary.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.volyVary.repository.UtilisateurRepository;

@Service
/**
 * Adaptateur entre Spring Security et la table utilisateur. Spring appelle ce service pendant la
 * connexion pour transformer un nom saisi dans le formulaire en objet UserDetails authentifiable.
 */
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    /**
     * Reçoit le repository des comptes. L'injection par constructeur rend la dépendance obligatoire
     * et facilite son remplacement pendant un test.
     */
    public CustomUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    /**
     * Recherche le compte correspondant au paramètre nom du formulaire. Une absence doit devenir
     * UsernameNotFoundException afin que Spring Security traite proprement l'échec de connexion.
     */
    public UserDetails loadUserByUsername(String nom) throws UsernameNotFoundException {
        return utilisateurRepository.findByNom(nom)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Aucun utilisateur trouvé avec le nom : " + nom));
    }
}
