package com.icwd.EmployeeService.services;

import com.icwd.EmployeeService.entities.Department;
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
public class DepartmentServiceImpl
        implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Department createDepartment(
            Department department
    ) {

        log.info(
                "Starting department creation"
        );

        if (department.getDepartmentId() == null
                || department.getDepartmentId().isBlank()) {

            String departmentId =
                    "DEP-" + UUID.randomUUID();

            department.setDepartmentId(
                    departmentId
            );

            log.debug(
                    "Generated department ID. departmentId={}",
                    departmentId
            );
        }

        Department savedDepartment =
                departmentRepository.save(
                        department
                );

        log.info(
                "Department created successfully. departmentId={}",
                savedDepartment.getDepartmentId()
        );

        return savedDepartment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {

        log.debug(
                "Fetching all departments from repository"
        );

        List<Department> departments =
                departmentRepository.findAll();

        log.info(
                "Departments fetched successfully. count={}",
                departments.size()
        );

        return departments;
    }

    @Override
    @Transactional(readOnly = true)
    public Department getDepartmentById(
            String departmentId
    ) {

        log.debug(
                "Fetching department by ID. departmentId={}",
                departmentId
        );

        return departmentRepository
                .findById(departmentId)
                .orElseThrow(() -> {

                    log.warn(
                            "Department not found. departmentId={}",
                            departmentId
                    );

                    return new ResourceNotFoundException(
                            "Department not found: "
                                    + departmentId
                    );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getDepartmentsByHotelId(
            String hotelId
    ) {

        log.debug(
                "Fetching departments by hotel. hotelId={}",
                hotelId
        );

        List<Department> departments =
                departmentRepository
                        .findByHotelId(
                                hotelId
                        );

        log.info(
                "Departments fetched successfully for hotel. hotelId={}, count={}",
                hotelId,
                departments.size()
        );

        return departments;
    }

    @Override
    public Department updateDepartment(
            String departmentId,
            Department updatedDepartment
    ) {

        log.info(
                "Starting department update. departmentId={}",
                departmentId
        );

        Department existingDepartment =
                getDepartmentById(
                        departmentId
                );

        existingDepartment.setDepartmentName(
                updatedDepartment.getDepartmentName()
        );

        existingDepartment.setHotelId(
                updatedDepartment.getHotelId()
        );

        existingDepartment.setDescription(
                updatedDepartment.getDescription()
        );

        Department savedDepartment =
                departmentRepository.save(
                        existingDepartment
                );

        log.info(
                "Department updated successfully. departmentId={}",
                departmentId
        );

        return savedDepartment;
    }

    @Override
    public void deleteDepartment(
            String departmentId
    ) {

        log.info(
                "Starting department deletion. departmentId={}",
                departmentId
        );

        Department department =
                getDepartmentById(
                        departmentId
                );

        log.debug(
                "Checking whether employees are assigned to department. departmentId={}",
                departmentId
        );

        boolean hasEmployees =
                employeeRepository
                        .existsByDepartmentDepartmentId(
                                departmentId
                        );

        if (hasEmployees) {

            log.warn(
                    "Department deletion blocked because employees are assigned. departmentId={}",
                    departmentId
            );

            throw new IllegalStateException(
                    "Cannot delete department because employees are assigned to it"
            );
        }

        departmentRepository.delete(
                department
        );

        log.info(
                "Department deleted successfully. departmentId={}",
                departmentId
        );
    }
}