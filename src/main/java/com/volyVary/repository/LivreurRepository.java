package com.volyVary.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.volyVary.model.Livreur;

@Repository
/**
 * Accès JPA aux livreurs avec une recherche insensible à la casse sur leur nom.
 */
public interface LivreurRepository extends JpaRepository<Livreur, Integer> {
    // Spring Data JPA genere automatiquement la requete SQL
    List<Livreur> findByNomContainingIgnoreCase(String nom);
}
