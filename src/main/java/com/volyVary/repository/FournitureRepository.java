package com.volyVary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.volyVary.model.Fourniture;

/**
 * Accès CRUD standard aux fournitures. JpaRepository fournit déjà save, findAll, findById et delete.
 */
public interface FournitureRepository extends JpaRepository<Fourniture, Integer> {
}
