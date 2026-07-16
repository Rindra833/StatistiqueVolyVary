package com.volyVary.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.volyVary.modele.LotPaddyTransforme;

@Repository
public interface LotPaddyTransformeRepository extends JpaRepository<LotPaddyTransforme, Integer>{
    /*
     * Calcule le prochain numéro à partir de la table réellement gérée par Hibernate. Cette requête
     * évite de dépendre d'une séquence créée uniquement lorsque database/table.sql est exécuté.
     */
    @Query(value = "SELECT COALESCE(MAX(id), 0) + 1 FROM lot_paddy_transforme", nativeQuery = true)
    Integer getProchainNumeroReference();

    LotPaddyTransforme findTopByOrderByIdDesc();
    LotPaddyTransforme findById(int id);

    @Query("SELECT COALESCE(SUM(l.quantite), 0) FROM LotPaddyTransforme l")
    double quantiteTotalTransformer();

    List<LotPaddyTransforme> findByDateBetween(LocalDateTime debut, LocalDateTime fin);

    List<LotPaddyTransforme> findByDateGreaterThanEqual(LocalDateTime debut);

    List<LotPaddyTransforme> findByDateLessThanEqual(LocalDateTime fin);

    List<LotPaddyTransforme> findByDate(LocalDateTime date);

    Page<LotPaddyTransforme> findByDateBetween(
            LocalDateTime debut,
            LocalDateTime fin,
            Pageable pageable
    );


    Page<LotPaddyTransforme> findByDateGreaterThanEqual(
            LocalDateTime debut,
            Pageable pageable
    );


    Page<LotPaddyTransforme> findByDateLessThanEqual(
            LocalDateTime fin,
            Pageable pageable
    );


    @Query("SELECT l FROM LotPaddyTransforme l WHERE LOWER(l.reference) LIKE LOWER(CONCAT('%', :reference, '%'))")
        Page<LotPaddyTransforme> rechercherParReference(@Param("reference") String reference, Pageable pageable);
}   
