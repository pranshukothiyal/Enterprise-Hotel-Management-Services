package com.icwd.EmployeeService.services;

import com.icwd.EmployeeService.entities.Employee;
import com.icwd.EmployeeService.entities.EmployeeStatus;

import java.util.List;

public interface EmployeeService {

    Employee createEmployee(Employee employee);

    List<Employee> getAllEmployees();

    Employee getEmployeeById(String employeeId);

    List<Employee> getEmployeesByDepartmentId(
            String departmentId
    );

    List<Employee> getEmployeesByHotelId(String hotelId);

    List<Employee> getEmployeesByStatus(
            EmployeeStatus employeeStatus
    );

    Employee updateEmployee(
            String employeeId,
            Employee employee
    );

    Employee updateEmployeeStatus(
            String employeeId,
            EmployeeStatus employeeStatus
    );

    void deleteEmployee(String employeeId);
}