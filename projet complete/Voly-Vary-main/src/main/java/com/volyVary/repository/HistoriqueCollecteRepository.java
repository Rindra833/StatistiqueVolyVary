package com.volyVary.repository;

import com.volyVary.modele.HistoriqueCollecte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoriqueCollecteRepository extends JpaRepository<HistoriqueCollecte, Integer> {
    @Query("SELECT h FROM HistoriqueCollecte h WHERE h.lotPaddy.idLotPaddy = :idLot ORDER BY h.idHistoriqueCollecte  DESC")
    List<HistoriqueCollecte> trouverDernierHistoriqueParLot(@Param("idLot") int idLot);  
}