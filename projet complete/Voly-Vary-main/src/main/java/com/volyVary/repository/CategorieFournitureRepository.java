package com.volyVary.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.volyVary.modele.CategorieFourniture;

@Repository
public interface CategorieFournitureRepository extends JpaRepository<CategorieFourniture, Integer> {

    List<CategorieFourniture> findAllByOrderByLibelleAsc();

    Optional<CategorieFourniture> findFirstByLibelleIgnoreCase(String libelle);
}
