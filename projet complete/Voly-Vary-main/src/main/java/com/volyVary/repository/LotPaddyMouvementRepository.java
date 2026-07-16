package com.volyVary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.volyVary.dto.LotStockDto;
import com.volyVary.modele.LotPaddyMouvement;

@Repository
public interface LotPaddyMouvementRepository extends JpaRepository<LotPaddyMouvement, Integer> {
    @Query("SELECT COALESCE(SUM(l.quantite),0.0) FROM LotPaddyMouvement l")
    Double getStockPaddy();

    @Query("SELECT COALESCE(SUM(l.quantite),0.0) FROM LotPaddyMouvement l WHERE l.lotPaddy =: id_lot")
    Double getStockByIdLotPaddy(@Param("id_lot") int id_lot);

    @Query("""
            SELECT new com.volyVary.dto.LotStockDto(
                l.idLotPaddy,
                (l.quantite - COALESCE(m.totalMouvement, 0.0)),
                l.reference
            )
            FROM LotPaddy l
            LEFT JOIN (
                SELECT m.lotPaddy.idLotPaddy AS idLot,
                       SUM(m.quantite) AS totalMouvement
                FROM LotPaddyMouvement m
                GROUP BY m.lotPaddy.idLotPaddy
            ) m ON m.idLot = l.idLotPaddy
            WHERE (l.quantite - COALESCE(m.totalMouvement, 0.0)) > 0
            ORDER BY l.idLotPaddy
            """)
    List<LotStockDto> getStockReelParLot();

    List<LotPaddyMouvement> findByLotPaddyTransformeId(int id);

}
