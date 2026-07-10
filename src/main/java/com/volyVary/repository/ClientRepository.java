package com.volyVary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.volyVary.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
