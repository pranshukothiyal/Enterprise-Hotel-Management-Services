package com.icwd.user.service.UserService.repositories;

import com.icwd.user.service.UserService.entitites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepositories extends JpaRepository<User, String> {

    // Custom finder method to grab a user by their custom ID field
//    User findUserByUserId(String userId);
//
//    // Custom JPQL Update Query
//    @Modifying
//    @Query("UPDATE User u SET u.name = :name, u.email = :email WHERE u.userId = :id")
//    int updateUserDetails(@Param("id") String id, @Param("name") String name, @Param("email") String email);

    Optional<User> findByEmailIgnoreCase(
            String email
    );
}