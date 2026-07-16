package com.volyVary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.volyVary.modele.DetailTransaction;

import java.util.List;

@Repository
public interface TransactionDetailRepository extends JpaRepository<DetailTransaction, Integer> {

    List<DetailTransaction> findAllByIdTransaction(int idTransaction);
}
