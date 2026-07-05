package com.volyVary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;

import com.volyVary.repository.UtilisateurRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // 1. Spring Security verifie le nom et le mdp
        Authentication authentication = authenticationManager.authentticate(
            new UserNamePasswordAuthenticationToken(loginRequest.getNom(), loginRequest.getMdp()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. On recupere l'utilisateur validé
        Utilisateur user = utilisateurRepository.findByNom(loginRequest.getNom()).get()

        // 3. On renvoie un JSON que le fichier auth.js pourra mettre dans sessionStorage 

        Map<String, Object> response = new HashMap<>();
        response.put("email", user.getNom()); // Le front utilise la clé 'email'[cite: 3]
        response.put("role", user.getRole());
        response.put("name", user.getNom());

        return ResponseEntity.ok(response);
    }
}