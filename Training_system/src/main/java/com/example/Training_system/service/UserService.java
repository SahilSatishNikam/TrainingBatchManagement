package com.example.Training_system.service;

import java.util.List;
import com.example.Training_system.dto.*;

public interface UserService {

	UserResponseDTO createAdmin(UserRequestDTO dto, String fileName);

	UserResponseDTO createTrainer(UserRequestDTO dto, String fileName);

	UserResponseDTO updateTrainer(Long id, UserRequestDTO dto, String fileName);

	String deleteTrainer(Long id);

	List<UserResponseDTO> getAllTrainerDTOs();
}