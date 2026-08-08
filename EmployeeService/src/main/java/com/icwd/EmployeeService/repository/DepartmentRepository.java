package com.icwd.EmployeeService.repository;

import com.icwd.EmployeeService.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository
        extends JpaRepository<Department, String> {

    List<Department> findByHotelId(String hotelId);
}