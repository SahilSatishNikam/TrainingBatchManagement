package com.example.Training_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Training_system.dto.*;
import com.example.Training_system.entity.*;
import com.example.Training_system.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ================= CREATE ADMIN =================
    @Override
    public UserResponseDTO createAdmin(UserRequestDTO dto, String fileName) {

        validateEmail(dto.getEmail(), null);
        validatePassword(dto.getPassword());

        User user = new User();
        mapDtoToEntity(dto, user);

        user.setRole(Role.ADMIN);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        setPhoto(user, fileName);

        return convert(userRepository.save(user));
    }

    // ================= CREATE TRAINER =================
    @Override
    public UserResponseDTO createTrainer(UserRequestDTO dto, String fileName) {

        validateEmail(dto.getEmail(), null);
        validatePassword(dto.getPassword());

        User user = new User();
        mapDtoToEntity(dto, user);

        user.setRole(Role.TRAINER);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        setPhoto(user, fileName);

        return convert(userRepository.save(user));
    }

    // ================= UPDATE TRAINER =================
    @Override
    public UserResponseDTO updateTrainer(Long id, UserRequestDTO dto, String fileName) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        mapDtoToEntity(dto, user);

        // update password only if provided
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        setPhoto(user, fileName);

        return convert(userRepository.save(user));
    }

    // ================= DELETE =================
    @Override
    public String deleteTrainer(Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Trainer not found");
        }

        userRepository.deleteById(id);
        return "Deleted successfully";
    }

    // ================= GET TRAINERS =================
    @Override
    public List<UserResponseDTO> getAllTrainerDTOs() {
        return userRepository.findByRole(Role.TRAINER)
                .stream()
                .map(this::convert)
                .toList();
    }

    // ================= COMMON METHODS =================

    private void validateEmail(String email, Long userId) {

        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email is required");
        }

        String normalizedEmail = email.trim().toLowerCase(); // ✅ FIX

        userRepository.findByEmailIgnoreCase(normalizedEmail)
                .ifPresent(existing -> {
                    if (userId == null || !existing.getId().equals(userId)) {
                        throw new RuntimeException("Email already exists");
                    }
                });
    }
    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new RuntimeException("Password is required");
        }
    }

    private void setPhoto(User user, String fileName) {
        if (fileName != null && !fileName.isBlank()) {
            user.setPhoto("/uploads/" + fileName);
        }
    }

    private void mapDtoToEntity(UserRequestDTO dto, User user) {

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail().trim().toLowerCase()); // ✅ FIX
        }

        user.setGender(dto.getGender());
        user.setMobile(dto.getMobile());
        user.setDepartment(defaultValue(dto.getDepartment()));
        user.setDesignation(dto.getDesignation());
        user.setAddress(dto.getAddress());
        user.setDob(dto.getDob());
        user.setJoiningDate(defaultValue(dto.getJoiningDate()));
        user.setEducation(dto.getEducation());

        user.setSalary(parseDouble(dto.getSalary()));
        user.setSubject(dto.getSubject());
        user.setExperience(parseInteger(dto.getExperience()));

        user.setStatus(dto.getStatus());
        user.setBio(dto.getBio());
    }

    private Double parseDouble(String value) {
        try {
            return (value != null && !value.isBlank()) ? Double.parseDouble(value) : null;
        } catch (Exception e) {
            throw new RuntimeException("Invalid salary");
        }
    }

    private Integer parseInteger(String value) {
        try {
            return (value != null && !value.isBlank()) ? Integer.parseInt(value) : null;
        } catch (Exception e) {
            throw new RuntimeException("Invalid experience");
        }
    }

    private String defaultValue(String value) {
        return value != null ? value : "";
    }

    private UserResponseDTO convert(User user) {

        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setPhoto(user.getPhoto());

        dto.setGender(user.getGender());
        dto.setMobile(user.getMobile());
        dto.setDepartment(user.getDepartment());
        dto.setDesignation(user.getDesignation());
        dto.setAddress(user.getAddress());
        dto.setDob(user.getDob());
        dto.setJoiningDate(user.getJoiningDate());
        dto.setEducation(user.getEducation());

        dto.setSalary(user.getSalary() != null ? user.getSalary().toString() : "");
        dto.setSubject(user.getSubject());
        dto.setExperience(user.getExperience() != null ? user.getExperience().toString() : "");
        dto.setStatus(user.getStatus());
        dto.setBio(user.getBio());

        return dto;
    }
}