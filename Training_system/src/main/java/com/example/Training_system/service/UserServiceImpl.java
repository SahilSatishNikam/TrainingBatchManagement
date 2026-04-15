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

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new RuntimeException("Password is required");
        }

        User user = new User();
        mapDtoToEntity(dto, user);

        user.setRole(Role.ADMIN);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (fileName != null) {
            user.setPhoto("/uploads/" + fileName);
        }

        return convert(userRepository.save(user));
    }

    // ================= CREATE TRAINER =================
    public  UserResponseDTO createTrainer(UserRequestDTO dto, String fileName) {

        User user = new User();

        user.setName(dto.getName());
        user.setLastName(dto.getLastName());
        user.setGender(dto.getGender());
        user.setMobile(dto.getMobile());

        // ✅ FIX: handle null safely
        user.setDepartment(dto.getDepartment() != null ? dto.getDepartment() : "");
        user.setJoiningDate(dto.getJoiningDate() != null ? dto.getJoiningDate() : "");

        user.setDesignation(dto.getDesignation());
        user.setAddress(dto.getAddress());
        user.setDob(dto.getDob());
        user.setEducation(dto.getEducation());
        user.setEmail(dto.getEmail());

        // ⚠️ Always encode password
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // ✅ SAFE parsing
        try {
            user.setSalary(dto.getSalary() != null && !dto.getSalary().isBlank()
                    ? Double.parseDouble(dto.getSalary())
                    : null);
        } catch (Exception e) {
            user.setSalary(null);
        }

        user.setSubject(dto.getSubject());

        try {
            user.setExperience(dto.getExperience() != null && !dto.getExperience().isBlank()
                    ? Integer.parseInt(dto.getExperience())
                    : null);
        } catch (Exception e) {
            user.setExperience(null);
        }

        user.setStatus(dto.getStatus());
        user.setBio(dto.getBio());

        // ✅ FIX: correct photo path
        if (fileName != null) {
            user.setPhoto("/uploads/" + fileName);
        }

        user.setRole(Role.TRAINER);

        return convert(userRepository.save(user));
    }
    // ================= UPDATE =================
    @Override
    public UserResponseDTO updateTrainer(Long id, UserRequestDTO dto, String fileName) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        if (dto.getEmail() != null) {
            validateEmail(dto.getEmail(), id);
        }

        mapDtoToEntity(dto, user);

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (fileName != null && !fileName.isBlank()) {
            user.setPhoto("/uploads/" + fileName);
        }

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

    // ================= EMAIL VALIDATION =================
    private void validateEmail(String email, Long userId) {

        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email is required");
        }

        userRepository.findByEmailIgnoreCase(email)
                .ifPresent(existing -> {
                    if (userId == null || !existing.getId().equals(userId)) {
                        throw new RuntimeException("Email already exists");
                    }
                });
    }

    // ================= MAPPING =================
    private void mapDtoToEntity(UserRequestDTO dto, User user) {

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());

        user.setGender(dto.getGender());
        user.setMobile(dto.getMobile());
        user.setDepartment(dto.getDepartment());
        user.setDesignation(dto.getDesignation());
        user.setAddress(dto.getAddress());
        user.setDob(dto.getDob());
        user.setJoiningDate(dto.getJoiningDate());
        user.setEducation(dto.getEducation());

        try {
            if (dto.getSalary() != null && !dto.getSalary().isBlank()) {
                user.setSalary(Double.parseDouble(dto.getSalary()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid salary");
        }

        user.setSubject(dto.getSubject());

        try {
            if (dto.getExperience() != null && !dto.getExperience().isBlank()) {
                user.setExperience(Integer.parseInt(dto.getExperience()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid experience");
        }

        user.setStatus(dto.getStatus());
        user.setBio(dto.getBio());
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