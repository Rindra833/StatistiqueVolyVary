package com.volyVary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.volyVary.model.Employee;

/**
 * Accès CRUD standard aux employés et source de la liste proposée dans le formulaire utilisateur.
 */
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
}
