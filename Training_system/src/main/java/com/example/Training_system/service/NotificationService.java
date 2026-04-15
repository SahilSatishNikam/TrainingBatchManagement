package com.example.Training_system.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.Training_system.entity.Notification;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendToAll(String message) {

        Notification notification = new Notification(message);

        System.out.println("📡 Broadcasting: " + message);

        messagingTemplate.convertAndSend(
                "/topic/notifications",
                notification
        );
    }
}