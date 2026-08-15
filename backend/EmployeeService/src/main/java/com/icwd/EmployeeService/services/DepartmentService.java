package com.icwd.EmployeeService.services;

import com.icwd.EmployeeService.entities.Department;

import java.util.List;

public interface DepartmentService {

    Department createDepartment(Department department);

    List<Department> getAllDepartments();

    Department getDepartmentById(String departmentId);

    List<Department> getDepartmentsByHotelId(String hotelId);

    Department updateDepartment(
            String departmentId,
            Department department
    );

    void deleteDepartment(String departmentId);
}