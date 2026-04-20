package com.example.Training_system.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Training_system.entity.User;
import com.example.Training_system.entity.Role;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    // ✅ BEST PRACTICE
    List<User> findByRole(Role role);
    
    long countByRole(Role trainer);
    
}