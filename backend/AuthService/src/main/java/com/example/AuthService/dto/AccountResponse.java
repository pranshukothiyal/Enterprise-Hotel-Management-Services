package com.example.AuthService.dto;

public class AccountResponse {


    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String role;

    private String userId;

    private String hotelId;

    private String employeeId;

    private String departmentId;


    public AccountResponse() {
    }


    public AccountResponse(
            String id,
            String firstName,
            String lastName,
            String email,
            String role,
            String userId,
            String hotelId,
            String employeeId,
            String departmentId
    ) {

        this.id = id;

        this.firstName = firstName;

        this.lastName = lastName;

        this.email = email;

        this.role = role;

        this.userId = userId;

        this.hotelId = hotelId;

        this.employeeId = employeeId;

        this.departmentId = departmentId;
    }


    public String getId() {

        return id;
    }


    public void setId(
            String id
    ) {

        this.id = id;
    }


    public String getFirstName() {

        return firstName;
    }


    public void setFirstName(
            String firstName
    ) {

        this.firstName = firstName;
    }


    public String getLastName() {

        return lastName;
    }


    public void setLastName(
            String lastName
    ) {

        this.lastName = lastName;
    }


    public String getEmail() {

        return email;
    }


    public void setEmail(
            String email
    ) {

        this.email = email;
    }


    public String getRole() {

        return role;
    }


    public void setRole(
            String role
    ) {

        this.role = role;
    }


    public String getUserId() {

        return userId;
    }


    public void setUserId(
            String userId
    ) {

        this.userId = userId;
    }


    public String getHotelId() {

        return hotelId;
    }


    public void setHotelId(
            String hotelId
    ) {

        this.hotelId = hotelId;
    }


    public String getEmployeeId() {

        return employeeId;
    }


    public void setEmployeeId(
            String employeeId
    ) {

        this.employeeId = employeeId;
    }


    public String getDepartmentId() {

        return departmentId;
    }


    public void setDepartmentId(
            String departmentId
    ) {

        this.departmentId = departmentId;
    }
}