package com.icwd.EmployeeService.services;

import com.icwd.EmployeeService.entities.Department;
import com.icwd.EmployeeService.entities.Employee;
import com.icwd.EmployeeService.entities.EmployeeStatus;
import com.icwd.EmployeeService.exception.ResourceNotFoundException;
import com.icwd.EmployeeService.repository.DepartmentRepository;
import com.icwd.EmployeeService.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl
        implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public Employee createEmployee(Employee employee) {

        log.info(
                "Starting employee creation"
        );

        if (employee.getDepartment() == null
                || employee.getDepartment()
                .getDepartmentId() == null) {

            log.warn(
                    "Employee creation failed because department ID is missing"
            );

            throw new IllegalArgumentException(
                    "Department ID is required"
            );
        }

        String departmentId =
                employee.getDepartment()
                        .getDepartmentId();

        log.debug(
                "Validating department for employee creation. departmentId={}",
                departmentId
        );

        Department department =
                departmentRepository
                        .findById(departmentId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Employee creation failed because department was not found. departmentId={}",
                                    departmentId
                            );

                            return new ResourceNotFoundException(
                                    "Department not found: "
                                            + departmentId
                            );
                        });

        if (employee.getEmployeeId() == null
                || employee.getEmployeeId().isBlank()) {

            String employeeId =
                    "EMP-" + UUID.randomUUID();

            employee.setEmployeeId(
                    employeeId
            );

            log.debug(
                    "Generated employee ID. employeeId={}",
                    employeeId
            );
        }

        if (employee.getEmployeeStatus() == null) {

            employee.setEmployeeStatus(
                    EmployeeStatus.ACTIVE
            );

            log.debug(
                    "Employee status was not provided. Default status set to ACTIVE. employeeId={}",
                    employee.getEmployeeId()
            );
        }

        employee.setDepartment(
                department
        );

        Employee savedEmployee =
                employeeRepository.save(
                        employee
                );

        log.info(
                "Employee created successfully. employeeId={}, departmentId={}, status={}",
                savedEmployee.getEmployeeId(),
                departmentId,
                savedEmployee.getEmployeeStatus()
        );

        return savedEmployee;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {

        log.debug(
                "Fetching all employees from repository"
        );

        List<Employee> employees =
                employeeRepository.findAll();

        log.info(
                "Employees fetched successfully. count={}",
                employees.size()
        );

        return employees;
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getEmployeeById(
            String employeeId
    ) {

        log.debug(
                "Fetching employee by ID. employeeId={}",
                employeeId
        );

        return employeeRepository
                .findById(employeeId)
                .orElseThrow(() -> {

                    log.warn(
                            "Employee not found. employeeId={}",
                            employeeId
                    );

                    return new ResourceNotFoundException(
                            "Employee not found: "
                                    + employeeId
                    );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByDepartmentId(
            String departmentId
    ) {

        log.debug(
                "Fetching employees by department. departmentId={}",
                departmentId
        );

        List<Employee> employees =
                employeeRepository
                        .findByDepartmentDepartmentId(
                                departmentId
                        );

        log.info(
                "Employees fetched successfully for department. departmentId={}, count={}",
                departmentId,
                employees.size()
        );

        return employees;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByHotelId(
            String hotelId
    ) {

        log.debug(
                "Fetching employees by hotel. hotelId={}",
                hotelId
        );

        List<Employee> employees =
                employeeRepository
                        .findByDepartmentHotelId(
                                hotelId
                        );

        log.info(
                "Employees fetched successfully for hotel. hotelId={}, count={}",
                hotelId,
                employees.size()
        );

        return employees;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByStatus(
            EmployeeStatus employeeStatus
    ) {

        log.debug(
                "Fetching employees by status. status={}",
                employeeStatus
        );

        List<Employee> employees =
                employeeRepository
                        .findByEmployeeStatus(
                                employeeStatus
                        );

        log.info(
                "Employees fetched successfully by status. status={}, count={}",
                employeeStatus,
                employees.size()
        );

        return employees;
    }

    @Override
    public Employee updateEmployee(
            String employeeId,
            Employee updatedEmployee
    ) {

        log.info(
                "Starting employee update. employeeId={}",
                employeeId
        );

        Employee existingEmployee =
                getEmployeeById(
                        employeeId
                );

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

            log.debug(
                    "Updating employee status during employee update. employeeId={}, newStatus={}",
                    employeeId,
                    updatedEmployee.getEmployeeStatus()
            );

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

            log.debug(
                    "Validating new department for employee update. employeeId={}, departmentId={}",
                    employeeId,
                    departmentId
            );

            Department department =
                    departmentRepository
                            .findById(departmentId)
                            .orElseThrow(() -> {

                                log.warn(
                                        "Employee update failed because department was not found. employeeId={}, departmentId={}",
                                        employeeId,
                                        departmentId
                                );

                                return new ResourceNotFoundException(
                                        "Department not found: "
                                                + departmentId
                                );
                            });

            existingEmployee.setDepartment(
                    department
            );

            log.debug(
                    "Employee department updated. employeeId={}, departmentId={}",
                    employeeId,
                    departmentId
            );
        }

        Employee savedEmployee =
                employeeRepository.save(
                        existingEmployee
                );

        log.info(
                "Employee updated successfully. employeeId={}, status={}",
                savedEmployee.getEmployeeId(),
                savedEmployee.getEmployeeStatus()
        );

        return savedEmployee;
    }

    @Override
    public Employee updateEmployeeStatus(
            String employeeId,
            EmployeeStatus employeeStatus
    ) {

        log.info(
                "Starting employee status update. employeeId={}, newStatus={}",
                employeeId,
                employeeStatus
        );

        Employee employee =
                getEmployeeById(
                        employeeId
                );

        employee.setEmployeeStatus(
                employeeStatus
        );

        Employee savedEmployee =
                employeeRepository.save(
                        employee
                );

        log.info(
                "Employee status updated successfully. employeeId={}, status={}",
                savedEmployee.getEmployeeId(),
                savedEmployee.getEmployeeStatus()
        );

        return savedEmployee;
    }

    @Override
    public void deleteEmployee(
            String employeeId
    ) {

        log.info(
                "Starting employee deletion. employeeId={}",
                employeeId
        );

        Employee employee =
                getEmployeeById(
                        employeeId
                );

        employeeRepository.delete(
                employee
        );

        log.info(
                "Employee deleted successfully. employeeId={}",
                employeeId
        );
    }
}