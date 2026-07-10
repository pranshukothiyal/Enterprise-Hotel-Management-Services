package com.icwd.user.service.UserService.services;

import com.icwd.user.service.UserService.entitites.User;
import java.util.List;

public interface UserServices {

    // Defining all user operations in this interface class

    // Creating a User
    User saveUser(User user);

    // Retrieving all the users.
    List<User> getAllUsers();

    // Retrieving a single user with the given userId
    User getuser(String userId);

    // Deleting the user with its userId
//    void deleteById(String userId);
//
//    // Updating user with its userId
//    User updateById(String userId, User user);
}