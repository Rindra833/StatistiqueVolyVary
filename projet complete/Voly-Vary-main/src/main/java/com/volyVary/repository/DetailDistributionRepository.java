package com.volyVary.repository;

import com.volyVary.modele.DetailDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetailDistributionRepository extends JpaRepository<DetailDistribution, Integer> {
    List<DetailDistribution> findByDistributionId(Integer idDistrib);

    @Query("SELECT COALESCE(SUM(d.quantite), 0) FROM DetailDistribution d")
    Double sommeQteTotale();

    @Query("SELECT COALESCE(SUM(d.quantite * d.produit.prixUnitaire), 0) FROM DetailDistribution d")
    Double sommeRecetteTotale();
}