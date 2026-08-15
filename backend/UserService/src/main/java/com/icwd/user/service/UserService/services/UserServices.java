package com.icwd.user.service.UserService.services;

import com.icwd.user.service.UserService.entitites.User;

import java.util.List;


public interface UserServices {


    // =========================================================
    // CREATE USER
    // =========================================================

    User saveUser(
            User user
    );


    // =========================================================
    // GET ALL USERS
    // =========================================================

    List<User> getAllUsers();


    // =========================================================
    // GET USER BY ID
    // =========================================================

    User getUser(
            String userId
    );


    // =========================================================
    // GET USER BY EMAIL
    // =========================================================

    User getUserByEmail(
            String email
    );
}