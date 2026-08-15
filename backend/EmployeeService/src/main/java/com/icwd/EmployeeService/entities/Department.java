package com.icwd.EmployeeService.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(
        name = "departments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_department_name_hotel",
                        columnNames = {
                                "department_name",
                                "hotel_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @Column(name = "department_id", nullable = false)
    private String departmentId;

    @NotBlank(message = "Department name is required")
    @Column(name = "department_name", nullable = false)
    private String departmentName;


    @NotBlank(message = "Hotel ID is required")
    @Column(name = "hotel_id", nullable = false)
    private String hotelId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}