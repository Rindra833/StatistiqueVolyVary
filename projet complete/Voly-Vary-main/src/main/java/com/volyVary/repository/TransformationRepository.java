package com.volyVary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.volyVary.modele.Transformation;

@Repository
public interface TransformationRepository extends JpaRepository<Transformation,Integer>{    
    Transformation findTopByOrderByIdTransformationDesc();
}
