package com.icwd.EmployeeService.payload;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse {

    private boolean success;

    private String message;

    private Object data;

    private LocalDateTime timestamp;
}