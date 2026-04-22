package com.example.Training_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Training_system.dto.*;
import com.example.Training_system.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService service;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = service.login(request);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(401)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}