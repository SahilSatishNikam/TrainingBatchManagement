package com.example.Training_system.service;

import com.example.Training_system.dto.*;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}