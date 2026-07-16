package com.volyVary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.volyVary.modele.DetailLotTransforme;
@Repository
public interface DetailLotTransformeRepository extends JpaRepository<DetailLotTransforme, Integer>{
    List<DetailLotTransforme> findByLotTransformeId(int id);

}