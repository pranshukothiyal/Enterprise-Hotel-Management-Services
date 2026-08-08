package com.icwd.EmployeeService.contollers;

import com.icwd.EmployeeService.entities.Department;
import com.icwd.EmployeeService.payload.ApiResponse;
import com.icwd.EmployeeService.services.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        departmentService
                                .createDepartment(department)
                );
    }

    @GetMapping
    public ResponseEntity<List<Department>>
    getAllDepartments() {

        return ResponseEntity.ok(
                departmentService.getAllDepartments()
        );
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<Department> getDepartmentById(
            @PathVariable String departmentId
    ) {
        return ResponseEntity.ok(
                departmentService
                        .getDepartmentById(departmentId)
        );
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Department>>
    getDepartmentsByHotelId(
            @PathVariable String hotelId
    ) {
        return ResponseEntity.ok(
                departmentService
                        .getDepartmentsByHotelId(hotelId)
        );
    }

    @PutMapping("/{departmentId}")
    public ResponseEntity<Department> updateDepartment(
            @PathVariable String departmentId,
            @Valid
            @RequestBody Department department
    ) {
        return ResponseEntity.ok(
                departmentService.updateDepartment(
                        departmentId,
                        department
                )
        );
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<ApiResponse> deleteDepartment(
            @PathVariable String departmentId
    ) {
        departmentService.deleteDepartment(departmentId);

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("Department deleted successfully")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}