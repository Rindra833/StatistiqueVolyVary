package com.volyVary.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.volyVary.dto.LoginRequest;
import com.volyVary.model.Utilisateur;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getNom(),
                    loginRequest.getMdp()
                )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();

            return ResponseEntity.ok(Map.of(
                "email", utilisateur.getNom(),
                "nom", utilisateur.getNom(),
                "role", utilisateur.getRole(),
                "name", utilisateur.getNom()
            ));
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Nom ou mot de passe incorrect"));
        }
    }
}
