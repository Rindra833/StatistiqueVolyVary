package com.volyVary.repository;

import com.volyVary.modele.Livreur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LivreurRepository extends JpaRepository<Livreur, Integer> {
    List<Livreur> findByMatriculeVehiculeContainingIgnoreCase(String nom);
}
