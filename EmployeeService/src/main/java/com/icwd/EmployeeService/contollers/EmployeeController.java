package com.icwd.EmployeeService.contollers;

import com.icwd.EmployeeService.entities.Employee;
import com.icwd.EmployeeService.entities.EmployeeStatus;
import com.icwd.EmployeeService.payload.ApiResponse;
import com.icwd.EmployeeService.services.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<Employee> createEmployee(
            @Valid
            @RequestBody Employee employee
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        employeeService
                                .createEmployee(employee)
                );
    }

    @GetMapping
    public ResponseEntity<List<Employee>>
    getAllEmployees() {

        return ResponseEntity.ok(
                employeeService.getAllEmployees()
        );
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<Employee> getEmployeeById(
            @PathVariable String employeeId
    ) {
        return ResponseEntity.ok(
                employeeService
                        .getEmployeeById(employeeId)
        );
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Employee>>
    getEmployeesByDepartment(
            @PathVariable String departmentId
    ) {
        return ResponseEntity.ok(
                employeeService
                        .getEmployeesByDepartmentId(
                                departmentId
                        )
        );
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Employee>>
    getEmployeesByHotel(
            @PathVariable String hotelId
    ) {
        return ResponseEntity.ok(
                employeeService
                        .getEmployeesByHotelId(hotelId)
        );
    }

    @GetMapping("/status/{employeeStatus}")
    public ResponseEntity<List<Employee>>
    getEmployeesByStatus(
            @PathVariable
            EmployeeStatus employeeStatus
    ) {
        return ResponseEntity.ok(
                employeeService
                        .getEmployeesByStatus(
                                employeeStatus
                        )
        );
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable String employeeId,
            @Valid
            @RequestBody Employee employee
    ) {
        return ResponseEntity.ok(
                employeeService.updateEmployee(
                        employeeId,
                        employee
                )
        );
    }

    @PatchMapping("/{employeeId}/status")
    public ResponseEntity<Employee> updateEmployeeStatus(
            @PathVariable String employeeId,
            @RequestParam EmployeeStatus status
    ) {
        return ResponseEntity.ok(
                employeeService.updateEmployeeStatus(
                        employeeId,
                        status
                )
        );
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<ApiResponse> deleteEmployee(
            @PathVariable String employeeId
    ) {
        employeeService.deleteEmployee(employeeId);

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("Employee deleted successfully")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}