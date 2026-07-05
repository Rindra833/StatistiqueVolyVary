package com.Hasner.VolyVary.repository;

import com.Hasner.VolyVary.model.MouvementProjet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MouvementProjetRepository extends JpaRepository<MouvementProjet, Long> {
    List<MouvementProjet> findByProjetIdAndDateOperationBetweenOrderByDateOperationAsc(Long projetId,
                                                                                       LocalDate dateDebut,
                                                                                       LocalDate dateFin);
}
