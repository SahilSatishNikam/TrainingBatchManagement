package com.example.Training_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Training_system.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRoleOrderByIdDesc(String role);
}