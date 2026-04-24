package com.example.Training_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.Training_system.entity.Notification;
import com.example.Training_system.entity.User;
import com.example.Training_system.repository.NotificationRepository;
import com.example.Training_system.repository.UserRepository;
import com.example.Training_system.service.NotificationService;

@RestController
@RequestMapping("/admin")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;


    // ✅ SEND TO ALL TRAINERS
    @PostMapping("/notify")
    public String send(@RequestParam String message) {

        List<String> phones = userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() != null && u.getRole().name().equals("TRAINER"))
                .map(User::getMobile)
                .filter(m -> m != null && !m.isBlank())
                .toList();

        // ✅ FIXED (Trainer topic)
        notificationService.sendToAllTrainers(message, phones);

        return "Notification sent to all trainers";
    }

    // ✅ SEND TO ONE TRAINER
    @PostMapping("/notify-trainer")
    public String sendToTrainer(
            @RequestParam String phone,
            @RequestParam String message) {

        // ✅ FIXED
        notificationService.sendToTrainer(phone, message);

        return "Notification sent to trainer";
    }

    // ✅ WHEN TRAINER UPDATES PROGRESS → ADMIN SHOULD GET
    @PostMapping("/notify-progress/{batchId}")
    public String notifyProgress(@PathVariable Long batchId) {

        String message = "📊 Progress updated for Batch ID: " + batchId;

        // 🔥 IMPORTANT FIX → SEND TO ADMIN
        notificationService.sendToAdmin(message);

        return "Progress notification sent to admin";
    }
    
    @GetMapping("/notifications")
    public List<Notification> getAdminNotifications() {
        return notificationRepository.findByRoleOrderByIdDesc("ADMIN");
    }
    
    @GetMapping("/notifications/trainer")
    public List<Notification> getTrainerNotifications() {
        return notificationRepository.findByRoleOrderByIdDesc("TRAINER");
    }
}