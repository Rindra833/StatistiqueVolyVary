package com.volyVary.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.volyVary.modele.Distribution;
import java.util.List;
public interface DistributionRepository extends JpaRepository<Distribution,Integer> {
    List<Distribution> findAllByOrderByDateDesc();
    boolean existsByLieu_Id(Integer idLieu);
    boolean existsByLivreur_Id(Integer idLivreur);
}
