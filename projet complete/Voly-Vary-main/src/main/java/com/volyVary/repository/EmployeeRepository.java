package com.volyVary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.volyVary.modele.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    
}
