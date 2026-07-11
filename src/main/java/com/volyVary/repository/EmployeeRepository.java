package com.volyVary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.volyVary.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
}
