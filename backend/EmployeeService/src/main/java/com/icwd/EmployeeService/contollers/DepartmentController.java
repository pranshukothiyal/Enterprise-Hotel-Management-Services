package com.icwd.EmployeeService.contollers;

import com.icwd.EmployeeService.entities.Department;
import com.icwd.EmployeeService.payload.ApiResponse;
import com.icwd.EmployeeService.services.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<Department> createDepartment(
            @Valid
            @RequestBody Department department
    ) {

        log.info(
                "Received request to create department"
        );

        Department createdDepartment =
                departmentService
                        .createDepartment(department);

        log.info(
                "Department created successfully. departmentId={}",
                createdDepartment.getDepartmentId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdDepartment);
    }

    @GetMapping
    public ResponseEntity<List<Department>>
    getAllDepartments() {

        log.info(
                "Received request to fetch all departments"
        );

        List<Department> departments =
                departmentService.getAllDepartments();

        log.debug(
                "Fetched all departments successfully. count={}",
                departments.size()
        );

        return ResponseEntity.ok(
                departments
        );
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<Department> getDepartmentById(
            @PathVariable String departmentId
    ) {

        log.info(
                "Received request to fetch department. departmentId={}",
                departmentId
        );

        Department department =
                departmentService
                        .getDepartmentById(departmentId);

        log.debug(
                "Department fetched successfully. departmentId={}",
                departmentId
        );

        return ResponseEntity.ok(
                department
        );
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Department>>
    getDepartmentsByHotelId(
            @PathVariable String hotelId
    ) {

        log.info(
                "Received request to fetch departments by hotel. hotelId={}",
                hotelId
        );

        List<Department> departments =
                departmentService
                        .getDepartmentsByHotelId(hotelId);

        log.debug(
                "Departments fetched successfully for hotel. hotelId={}, count={}",
                hotelId,
                departments.size()
        );

        return ResponseEntity.ok(
                departments
        );
    }

    @PutMapping("/{departmentId}")
    public ResponseEntity<Department> updateDepartment(
            @PathVariable String departmentId,
            @Valid
            @RequestBody Department department
    ) {

        log.info(
                "Received request to update department. departmentId={}",
                departmentId
        );

        Department updatedDepartment =
                departmentService.updateDepartment(
                        departmentId,
                        department
                );

        log.info(
                "Department updated successfully. departmentId={}",
                departmentId
        );

        return ResponseEntity.ok(
                updatedDepartment
        );
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<ApiResponse> deleteDepartment(
            @PathVariable String departmentId
    ) {

        log.info(
                "Received request to delete department. departmentId={}",
                departmentId
        );

        departmentService.deleteDepartment(
                departmentId
        );

        log.info(
                "Department deleted successfully. departmentId={}",
                departmentId
        );

        ApiResponse response =
                ApiResponse.builder()
                        .success(true)
                        .message(
                                "Department deleted successfully"
                        )
                        .timestamp(
                                LocalDateTime.now()
                        )
                        .build();

        return ResponseEntity.ok(
                response
        );
    }
}