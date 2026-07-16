package com.volyVary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.volyVary.modele.Produit;

@Repository
public interface ProduitRepository extends JpaRepository<Produit,Integer>{
    
}
