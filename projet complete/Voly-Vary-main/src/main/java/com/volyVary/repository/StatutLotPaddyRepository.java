package com.volyVary.repository;

import com.volyVary.modele.StatutLotPaddy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutLotPaddyRepository extends JpaRepository<StatutLotPaddy, Integer> {
    @Query("SELECT s FROM StatutLotPaddy s WHERE s.sigle = :sigle")
    StatutLotPaddy trouverParSigle(String sigle);
}