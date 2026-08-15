package com.icwd.EmployeeService.repository;

import com.icwd.EmployeeService.entities.Employee;
import com.icwd.EmployeeService.entities.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository
        extends JpaRepository<Employee, String> {

    List<Employee> findByDepartmentDepartmentId(
            String departmentId
    );

    List<Employee> findByDepartmentHotelId(
            String hotelId
    );

    List<Employee> findByEmployeeStatus(
            EmployeeStatus employeeStatus
    );

    boolean existsByDepartmentDepartmentId(
            String departmentId
    );
}