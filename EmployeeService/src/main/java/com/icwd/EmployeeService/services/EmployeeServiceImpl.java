package com.icwd.EmployeeService.services;

import com.icwd.EmployeeService.entities.Department;
import com.icwd.EmployeeService.entities.Employee;
import com.icwd.EmployeeService.entities.EmployeeStatus;
import com.icwd.EmployeeService.exception.ResourceNotFoundException;
import com.icwd.EmployeeService.repository.DepartmentRepository;
import com.icwd.EmployeeService.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl
        implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public Employee createEmployee(Employee employee) {

        if (employee.getDepartment() == null
                || employee.getDepartment()
                .getDepartmentId() == null) {

            throw new IllegalArgumentException(
                    "Department ID is required"
            );
        }

        String departmentId =
                employee.getDepartment()
                        .getDepartmentId();

        Department department =
                departmentRepository
                        .findById(departmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Department not found: "
                                                + departmentId
                                )
                        );

        if (employee.getEmployeeId() == null
                || employee.getEmployeeId().isBlank()) {

            employee.setEmployeeId(
                    "EMP-" + UUID.randomUUID()
            );
        }

        if (employee.getEmployeeStatus() == null) {
            employee.setEmployeeStatus(
                    EmployeeStatus.ACTIVE
            );
        }

        employee.setDepartment(department);

        return employeeRepository.save(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getEmployeeById(String employeeId) {
        return employeeRepository
                .findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found: "
                                        + employeeId
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByDepartmentId(
            String departmentId
    ) {
        return employeeRepository
                .findByDepartmentDepartmentId(
                        departmentId
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByHotelId(
            String hotelId
    ) {
        return employeeRepository
                .findByDepartmentHotelId(hotelId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByStatus(
            EmployeeStatus employeeStatus
    ) {
        return employeeRepository
                .findByEmployeeStatus(employeeStatus);
    }

    @Override
    public Employee updateEmployee(
            String employeeId,
            Employee updatedEmployee
    ) {
        Employee existingEmployee =
                getEmployeeById(employeeId);

        existingEmployee.setEmployeeName(
                updatedEmployee.getEmployeeName()
        );

        existingEmployee.setEmail(
                updatedEmployee.getEmail()
        );

        existingEmployee.setPhone(
                updatedEmployee.getPhone()
        );

        existingEmployee.setDesignation(
                updatedEmployee.getDesignation()
        );

        existingEmployee.setSalary(
                updatedEmployee.getSalary()
        );

        if (updatedEmployee.getEmployeeStatus() != null) {
            existingEmployee.setEmployeeStatus(
                    updatedEmployee.getEmployeeStatus()
            );
        }

        if (updatedEmployee.getDepartment() != null
                && updatedEmployee.getDepartment()
                .getDepartmentId() != null) {

            String departmentId =
                    updatedEmployee.getDepartment()
                            .getDepartmentId();

            Department department =
                    departmentRepository
                            .findById(departmentId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Department not found: "
                                                    + departmentId
                                    )
                            );

            existingEmployee.setDepartment(department);
        }

        return employeeRepository.save(existingEmployee);
    }

    @Override
    public Employee updateEmployeeStatus(
            String employeeId,
            EmployeeStatus employeeStatus
    ) {
        Employee employee =
                getEmployeeById(employeeId);

        employee.setEmployeeStatus(employeeStatus);

        return employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployee(String employeeId) {
        Employee employee =
                getEmployeeById(employeeId);

        employeeRepository.delete(employee);
    }
}