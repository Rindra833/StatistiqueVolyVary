package com.volyVary.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.volyVary.model.Utilisateur;

@Repository
/**
 * Accès JPA aux comptes de connexion. findByNom est utilisé par l'authentification et par les
 * contrôles d'unicité lors de la création d'un compte.
 */
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    Optional<Utilisateur> findByNom(String nom);
}
