package com.volyVary.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.volyVary.modele.StatutTransaction;

@Repository
public interface StatutTransactionRepository extends JpaRepository<StatutTransaction, Integer> {

    Optional<StatutTransaction> findBySigle(String sigle);
}
