package com.volyVary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.volyVary.modele.Collecte;

@Repository
public interface CollecteRepository extends JpaRepository<Collecte, Integer> {
    
}