package com.volyVary.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivreurRepository extends JpaRepository<Livreur, Long> {
    // Spring Data JPA genere automatiquement la requete SQL
    List<Livreur> findByNomContainingIgnoreCase(String nom);
}