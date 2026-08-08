package com.icwd.EmployeeService.services;

import com.icwd.EmployeeService.entities.Department;
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
public class DepartmentServiceImpl
        implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Department createDepartment(
            Department department
    ) {
        if (department.getDepartmentId() == null
                || department.getDepartmentId().isBlank()) {

            department.setDepartmentId(
                    "DEP-" + UUID.randomUUID()
            );
        }

        return departmentRepository.save(department);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Department getDepartmentById(
            String departmentId
    ) {
        return departmentRepository
                .findById(departmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found: "
                                        + departmentId
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getDepartmentsByHotelId(
            String hotelId
    ) {
        return departmentRepository.findByHotelId(hotelId);
    }

    @Override
    public Department updateDepartment(
            String departmentId,
            Department updatedDepartment
    ) {
        Department existingDepartment =
                getDepartmentById(departmentId);

        existingDepartment.setDepartmentName(
                updatedDepartment.getDepartmentName()
        );

        existingDepartment.setHotelId(
                updatedDepartment.getHotelId()
        );

        existingDepartment.setDescription(
                updatedDepartment.getDescription()
        );

        return departmentRepository.save(
                existingDepartment
        );
    }

    @Override
    public void deleteDepartment(String departmentId) {
        Department department =
                getDepartmentById(departmentId);

        boolean hasEmployees =
                employeeRepository
                        .existsByDepartmentDepartmentId(
                                departmentId
                        );

        if (hasEmployees) {
            throw new IllegalStateException(
                    "Cannot delete department because employees are assigned to it"
            );
        }

        departmentRepository.delete(department);
    }
}