package com.volyVary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.volyVary.model.Client;

/**
 * Accès CRUD standard aux clients persistés dans PostgreSQL.
 */
public interface ClientRepository extends JpaRepository<Client, Integer> {
}
