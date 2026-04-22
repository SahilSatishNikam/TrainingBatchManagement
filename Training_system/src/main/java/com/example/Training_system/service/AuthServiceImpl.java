package com.example.Training_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Training_system.dto.*;
import com.example.Training_system.entity.User;
import com.example.Training_system.repository.UserRepository;
import com.example.Training_system.security.JwtUtil;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public LoginResponse login(LoginRequest req) {

        if (req.getEmail() == null || req.getPassword() == null) {
            throw new RuntimeException("Email and password required");
        }

        String email = req.getEmail().trim().toLowerCase();

        User user = repo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginResponse(token, user.getRole().toString());
    }
}