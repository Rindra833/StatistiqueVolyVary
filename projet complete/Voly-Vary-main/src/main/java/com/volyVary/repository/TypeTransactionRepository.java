package com.volyVary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.volyVary.modele.TypeTransaction;

@Repository
public interface TypeTransactionRepository extends JpaRepository<TypeTransaction, Integer> {

    List<TypeTransaction> findAllByOrderByLibelleAsc();
}
