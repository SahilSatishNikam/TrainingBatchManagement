package com.example.Training_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Training_system.entity.Batch;
import com.example.Training_system.entity.User;
import com.example.Training_system.repository.UserRepository;
import com.example.Training_system.service.NotificationService;


@RestController
@RequestMapping("/admin")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    // ✅ SEND TO ALL TRAINERS
    @PostMapping("/notify")
    public String send(@RequestParam String message) {

        // 🔥 Get all trainer phones
        List<String> phones = userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() != null && u.getRole().name().equals("TRAINER"))
                .map(User::getMobile)
                .filter(m -> m != null && !m.isBlank())
                .toList();

        // ✅ Send BOTH (UI + WhatsApp)
        notificationService.sendToAll(message, phones);

        return "Notification sent to all trainers";
    }

    // ✅ SEND TO ONE TRAINER
    @PostMapping("/notify-trainer")
    public String sendToTrainer(
            @RequestParam String phone,
            @RequestParam String message) {

        notificationService.sendToTrainer(phone, message);

        return "Notification sent to trainer";
    }
    
    @PostMapping("/notify-progress/{batchId}")
    public String notifyProgress(@PathVariable Long batchId) {

        String message = "📊 Progress updated for Batch ID: " + batchId;

        // ✅ ONLY UI (NO WhatsApp/SMS)
        notificationService.sendToUI(message);

        return "Progress notification sent";
    }
}