package com.example.AuthService.dto;

public class UserProfileRequest {

    private String name;
    private String email;
    private String about;


    public UserProfileRequest() {
    }


    public UserProfileRequest(
            String name,
            String email,
            String about
    ) {
        this.name = name;
        this.email = email;
        this.about = about;
    }


    public String getName() {
        return name;
    }


    public void setName(
            String name
    ) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(
            String email
    ) {
        this.email = email;
    }


    public String getAbout() {
        return about;
    }


    public void setAbout(
            String about
    ) {
        this.about = about;
    }
}