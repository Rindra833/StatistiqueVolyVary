package com.volyVary.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.volyVary.modele.StatutDistribution;
public interface StatutDistributionRepository extends JpaRepository<StatutDistribution, Integer> {
    Optional<StatutDistribution> findBySigle(String sigle);
}