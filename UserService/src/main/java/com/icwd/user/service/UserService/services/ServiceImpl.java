package com.icwd.user.service.UserService.services;

import com.icwd.user.service.UserService.entitites.User;
import com.icwd.user.service.UserService.exception.ResourceNotFoundException;
import com.icwd.user.service.UserService.repositories.UserRepositories;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ServiceImpl implements UserServices {

    @Autowired
    private UserRepositories userRepositories;

    // Implementing the user saving method
    @Override
    public User saveUser(User user) {

        //it will generate unique userID
      String randomUserId = UUID.randomUUID().toString();
      user.setUserId(randomUserId);
        return userRepositories.save(user);
    }

    // Implementing the retrieval of all users
    @Override
    public List<User> getAllUsers() {
        return userRepositories.findAll();
    }

    // Implementing single user retrieval
    @Override
    public User getuser(String userId) {
        return userRepositories.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User with given id is not found on server"));
    }

//    // Implementing deletion using JpaRepository's built-in method
//    @Override
//    public void deleteById(String userId) {
//        userRepositories.deleteById(userId);
//    }
//
//    // Implementing user update using custom JPQL method
//    @Override
//    public User updateById(String userId, User user) {
//        int rowsAffected = userRepositories.updateUserDetails(
//                userId,
//                user.getName(),
//                user.getEmail()
//        );
//
//        if (rowsAffected == 0) {
//            throw new RuntimeException("User not found with ID: " + userId);
//        }
//
//        // Return the freshly updated user record from the database
//        return userRepositories.findById(userId).orElse(null);
//    }
}