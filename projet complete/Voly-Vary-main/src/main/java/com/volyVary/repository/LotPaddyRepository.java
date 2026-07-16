package com.volyVary.repository;

import com.volyVary.modele.LotPaddy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

@Repository
public interface LotPaddyRepository extends JpaRepository<LotPaddy, Integer> {

    @Query("""
        SELECT SUM(lo.quantite) FROM LotPaddy lo
        JOIN HistoriqueCollecte h ON h.lotPaddy = lo
        WHERE h.idHistoriqueCollecte = (
            SELECT MAX(h2.idHistoriqueCollecte)
            FROM HistoriqueCollecte h2
            WHERE h2.lotPaddy = lo
        )
        AND h.statut.sigle <> 'ANNULE'
    """)
    Double obtenirQuantiteTotal();



    @Query("""
        SELECT SUM(lo.prixCollecte) FROM LotPaddy lo
        JOIN HistoriqueCollecte h ON h.lotPaddy = lo
        WHERE h.idHistoriqueCollecte = (
            SELECT MAX(h2.idHistoriqueCollecte)
            FROM HistoriqueCollecte h2
            WHERE h2.lotPaddy = lo
        )
        AND h.statut.sigle <> 'ANNULE'
    """)
    Double obtenirRecetteTotal();
    

    @Query("SELECT lo FROM LotPaddy lo ORDER BY lo.date DESC")
    List<LotPaddy> trouverToutDateDec();

    
    @Query("SELECT lo FROM LotPaddy lo WHERE lo.idLotPaddy = :id")
    LotPaddy trouverParIdLotPaddy(@Param("id") int id);


    @Query("""
        SELECT lo FROM LotPaddy lo
        JOIN HistoriqueCollecte h ON h.lotPaddy = lo
        WHERE h.idHistoriqueCollecte = (
            SELECT MAX(h2.idHistoriqueCollecte)
            FROM HistoriqueCollecte h2
            WHERE h2.lotPaddy = lo
        )
        AND h.statut.sigle <> 'ANNULE'
        ORDER BY lo.date DESC
    """)
    List<LotPaddy> trouverLotsActif();

    @Query("SELECT SUM(l.quantite) FROM LotPaddy l")
    Double sommeQuantite();

    @Query("SELECT quantite FROM LotPaddy l WHERE l.id =: id_lot")
    Double getLotPaddyByIdLot(@Param("id_lot") int id_lot);
}