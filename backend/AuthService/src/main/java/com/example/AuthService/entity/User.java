package com.example.AuthService.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User implements UserDetails {


    // =========================================================
    // AUTH USER ID
    // =========================================================

    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    private String id;


    // =========================================================
    // BASIC DETAILS
    // =========================================================

    private String firstName;

    private String lastName;


    @Column(
            unique = true,
            nullable = false
    )
    private String email;


    private String password;


    // =========================================================
    // ROLE
    // =========================================================

    @Enumerated(EnumType.STRING)
    private Role role;


    // =========================================================
    // USER SERVICE LINK
    // =========================================================

    /*
     * Used mainly for GUEST accounts.
     *
     * Example:
     * 23b5b8c1-...
     */
    private String userId;


    // =========================================================
    // HOTEL SCOPE
    // =========================================================

    /*
     * Used mainly for HOTEL_MANAGER and EMPLOYEE.
     *
     * Example:
     * HTL-001
     */
    private String hotelId;


    // =========================================================
    // EMPLOYEE SERVICE LINK
    // =========================================================

    /*
     * Used for EMPLOYEE accounts.
     */
    private String employeeId;


    // =========================================================
    // DEPARTMENT LINK
    // =========================================================

    /*
     * Used for EMPLOYEE accounts.
     */
    private String departmentId;


    // =========================================================
    // SPRING SECURITY AUTHORITIES
    // =========================================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(

                new SimpleGrantedAuthority(
                        "ROLE_" + role.name()
                )
        );
    }


    @Override
    public String getUsername() {

        return email;
    }


    @Override
    public boolean isAccountNonExpired() {

        return true;
    }


    @Override
    public boolean isAccountNonLocked() {

        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }


    @Override
    public boolean isEnabled() {

        return true;
    }
}