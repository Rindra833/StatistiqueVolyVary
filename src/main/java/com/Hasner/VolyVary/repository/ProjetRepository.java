package com.Hasner.VolyVary.repository;

import com.Hasner.VolyVary.model.Projet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjetRepository extends JpaRepository<Projet, Long> {
    List<Projet> findByUtilisateurNomOrderByNomAsc(String nom);

    Optional<Projet> findFirstByUtilisateurNomOrderByIdAsc(String nom);
}
