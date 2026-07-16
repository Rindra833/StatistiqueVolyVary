package com.volyVary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.volyVary.modele.Fourniture;

@Repository
public interface FournitureRepository extends JpaRepository<Fourniture, Integer> {

    @Query("select f from Fourniture f where f.idCategorie = :idCategorie order by f.reference asc")
    List<Fourniture> findByCategory(@Param("idCategorie") int idCategorie);

    boolean existsByIdCategorie(int idCategorie);
}
