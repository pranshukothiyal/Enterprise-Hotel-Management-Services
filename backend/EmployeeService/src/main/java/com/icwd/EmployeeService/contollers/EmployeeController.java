package com.icwd.EmployeeService.contollers;

import com.icwd.EmployeeService.entities.Employee;
import com.icwd.EmployeeService.entities.EmployeeStatus;
import com.icwd.EmployeeService.payload.ApiResponse;
import com.icwd.EmployeeService.services.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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

        log.info(
                "Received request to create employee"
        );

        Employee createdEmployee =
                employeeService.createEmployee(
                        employee
                );

        log.info(
                "Employee created successfully"
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdEmployee);
    }

    @GetMapping
    public ResponseEntity<List<Employee>>
    getAllEmployees() {

        log.info(
                "Received request to fetch all employees"
        );

        List<Employee> employees =
                employeeService.getAllEmployees();

        log.debug(
                "Fetched all employees successfully. count={}",
                employees.size()
        );

        return ResponseEntity.ok(
                employees
        );
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<Employee> getEmployeeById(
            @PathVariable String employeeId
    ) {

        log.info(
                "Received request to fetch employee. employeeId={}",
                employeeId
        );

        Employee employee =
                employeeService
                        .getEmployeeById(
                                employeeId
                        );

        log.debug(
                "Employee fetched successfully. employeeId={}",
                employeeId
        );

        return ResponseEntity.ok(
                employee
        );
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Employee>>
    getEmployeesByDepartment(
            @PathVariable String departmentId
    ) {

        log.info(
                "Received request to fetch employees by department. departmentId={}",
                departmentId
        );

        List<Employee> employees =
                employeeService
                        .getEmployeesByDepartmentId(
                                departmentId
                        );

        log.debug(
                "Employees fetched successfully for department. departmentId={}, count={}",
                departmentId,
                employees.size()
        );

        return ResponseEntity.ok(
                employees
        );
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Employee>>
    getEmployeesByHotel(
            @PathVariable String hotelId
    ) {

        log.info(
                "Received request to fetch employees by hotel. hotelId={}",
                hotelId
        );

        List<Employee> employees =
                employeeService
                        .getEmployeesByHotelId(
                                hotelId
                        );

        log.debug(
                "Employees fetched successfully for hotel. hotelId={}, count={}",
                hotelId,
                employees.size()
        );

        return ResponseEntity.ok(
                employees
        );
    }

    @GetMapping("/status/{employeeStatus}")
    public ResponseEntity<List<Employee>>
    getEmployeesByStatus(
            @PathVariable
            EmployeeStatus employeeStatus
    ) {

        log.info(
                "Received request to fetch employees by status. status={}",
                employeeStatus
        );

        List<Employee> employees =
                employeeService
                        .getEmployeesByStatus(
                                employeeStatus
                        );

        log.debug(
                "Employees fetched successfully by status. status={}, count={}",
                employeeStatus,
                employees.size()
        );

        return ResponseEntity.ok(
                employees
        );
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable String employeeId,
            @Valid
            @RequestBody Employee employee
    ) {

        log.info(
                "Received request to update employee. employeeId={}",
                employeeId
        );

        Employee updatedEmployee =
                employeeService.updateEmployee(
                        employeeId,
                        employee
                );

        log.info(
                "Employee updated successfully. employeeId={}",
                employeeId
        );

        return ResponseEntity.ok(
                updatedEmployee
        );
    }

    @PatchMapping("/{employeeId}/status")
    public ResponseEntity<Employee> updateEmployeeStatus(
            @PathVariable String employeeId,
            @RequestParam EmployeeStatus status
    ) {

        log.info(
                "Received request to update employee status. employeeId={}, newStatus={}",
                employeeId,
                status
        );

        Employee updatedEmployee =
                employeeService.updateEmployeeStatus(
                        employeeId,
                        status
                );

        log.info(
                "Employee status updated successfully. employeeId={}, newStatus={}",
                employeeId,
                status
        );

        return ResponseEntity.ok(
                updatedEmployee
        );
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<ApiResponse> deleteEmployee(
            @PathVariable String employeeId
    ) {

        log.info(
                "Received request to delete employee. employeeId={}",
                employeeId
        );

        employeeService.deleteEmployee(
                employeeId
        );

        log.info(
                "Employee deleted successfully. employeeId={}",
                employeeId
        );

        ApiResponse response =
                ApiResponse.builder()
                        .success(true)
                        .message(
                                "Employee deleted successfully"
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