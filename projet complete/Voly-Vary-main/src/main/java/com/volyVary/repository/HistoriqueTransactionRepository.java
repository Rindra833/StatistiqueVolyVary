package com.volyVary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.volyVary.modele.HistoriqueTransaction;

import java.util.List;

@Repository
public interface HistoriqueTransactionRepository extends JpaRepository<HistoriqueTransaction, Integer> {

    List<HistoriqueTransaction> findAllByIdTransactionOrderByDateAsc(int idTransaction);
}
