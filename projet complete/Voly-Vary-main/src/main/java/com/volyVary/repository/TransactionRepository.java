package com.volyVary.repository;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.volyVary.modele.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Transaction findFirstByOrderByIdDesc();
    List<Transaction> findAllByOrderByDateDesc();

    @Query(
        value = """
            SELECT t.*
            FROM transaction t
            JOIN client c ON c.id = t.id_client
            JOIN type_transaction tt ON tt.id = t.id_type
            WHERE LOWER(t.reference) LIKE LOWER(CONCAT('%', COALESCE(:reference, ''), '%'))
              AND LOWER(c.reference) LIKE LOWER(CONCAT('%', COALESCE(:referenceClient, ''), '%'))
              AND tt.id = COALESCE(:typeTransactionId, tt.id)
              AND t.date >= COALESCE(:dateDebut, TIMESTAMP '1970-01-01 00:00:00')
              AND t.date <= COALESCE(:dateFin, TIMESTAMP '2099-12-31 23:59:59')
            ORDER BY t.date DESC
        """,
        countQuery = """
            SELECT COUNT(*)
            FROM transaction t
            JOIN client c ON c.id = t.id_client
            JOIN type_transaction tt ON tt.id = t.id_type
            WHERE LOWER(t.reference) LIKE LOWER(CONCAT('%', COALESCE(:reference, ''), '%'))
              AND LOWER(c.reference) LIKE LOWER(CONCAT('%', COALESCE(:referenceClient, ''), '%'))
              AND tt.id = COALESCE(:typeTransactionId, tt.id)
              AND t.date >= COALESCE(:dateDebut, TIMESTAMP '1970-01-01 00:00:00')
              AND t.date <= COALESCE(:dateFin, TIMESTAMP '2099-12-31 23:59:59')
        """,
        nativeQuery = true
    )
    Page<Transaction> searchTransactions(
        @org.springframework.data.repository.query.Param("reference") String reference,
        @org.springframework.data.repository.query.Param("referenceClient") String referenceClient,
        @org.springframework.data.repository.query.Param("typeTransactionId") Integer typeTransactionId,
        @org.springframework.data.repository.query.Param("dateDebut") LocalDateTime dateDebut,
        @org.springframework.data.repository.query.Param("dateFin") LocalDateTime dateFin,
        Pageable pageable
    );

    @Query("select coalesce(sum(d.quantite), 0) from DetailTransaction d")
    int getTotalQuantitySold();

    @Query("select coalesce(sum(t.montantTotal), 0) from Transaction t")
    double getTotalRevenue();
}
