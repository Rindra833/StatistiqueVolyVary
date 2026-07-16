package com.volyVary.repository;

import com.volyVary.modele.HistoriqueDistribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriqueDistributionRepository extends JpaRepository<HistoriqueDistribution, Integer> {
    List<HistoriqueDistribution> findByDistributionIdOrderByDateAsc(Integer distributionId);
}