package com.volyVary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.volyVary.model.Fourniture;

public interface FournitureRepository extends JpaRepository<Fourniture, Integer> {
}
