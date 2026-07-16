package com.volyVary.repository;

import com.volyVary.modele.Reduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ReductionRepository extends JpaRepository<Reduction, Integer> {   
    @Query("SELECT r FROM Reduction r WHERE :taux >= r.humidite1 AND :taux < r.humidite2")
    Reduction trouverReductionParTaux(@Param("taux") Double tauxHumidite);
}