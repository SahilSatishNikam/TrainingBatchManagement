package com.example.Training_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.Training_system.entity.Notification;
import com.example.Training_system.repository.NotificationRepository;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final WhatsAppService whatsAppService;
    @Autowired
    private NotificationRepository notificationRepository;

    public NotificationService(SimpMessagingTemplate messagingTemplate,
                               WhatsAppService whatsAppService) {
        this.messagingTemplate = messagingTemplate;
        this.whatsAppService = whatsAppService;
    }

    // ✅ SEND TO ALL TRAINERS
    public void sendToAllTrainers(String message, List<String> phones) {

        Notification notif = new Notification(message, "TRAINER");

        notificationRepository.save(notif); // ✅ SAVE

        messagingTemplate.convertAndSend(
                "/topic/trainer-notifications",
                notif
        );

        for (String phone : phones) {
            whatsAppService.sendWhatsApp(phone, message);
        }
    }

    // ✅ SEND TO ONE TRAINER
    public void sendToTrainer(String phone, String message) {

        Notification notif = new Notification(message, "TRAINER");

        notificationRepository.save(notif);

        messagingTemplate.convertAndSend(
                "/topic/trainer-notifications",
                notif
        );

        whatsAppService.sendWhatsApp(phone, message);
    }
    // ✅ SEND TO ADMIN ONLY
    public void sendToAdmin(String message) {

        Notification notif = new Notification(message, "ADMIN");

        notificationRepository.save(notif); // ✅ SAVE TO DB

        messagingTemplate.convertAndSend(
                "/topic/admin-notifications",
                notif
        );
    }

    // ✅ SEND TO BOTH (OPTIONAL)
    public void sendToAllUsers(String message) {

        // Trainer
        Notification trainerNotif = new Notification(message, "TRAINER");
        notificationRepository.save(trainerNotif);

        messagingTemplate.convertAndSend(
                "/topic/trainer-notifications",
                trainerNotif
        );

        // Admin
        Notification adminNotif = new Notification(message, "ADMIN");
        notificationRepository.save(adminNotif);

        messagingTemplate.convertAndSend(
                "/topic/admin-notifications",
                adminNotif
        );
    }
}