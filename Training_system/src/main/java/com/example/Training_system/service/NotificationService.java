package com.example.Training_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.Training_system.entity.Notification;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final WhatsAppService whatsAppService;
    private final SmsService smsService; // ✅ NEW

    public NotificationService(SimpMessagingTemplate messagingTemplate,
                               WhatsAppService whatsAppService,
                               SmsService smsService) {
        this.messagingTemplate = messagingTemplate;
        this.whatsAppService = whatsAppService;
        this.smsService = smsService;
    }

    // ✅ SEND TO ALL TRAINERS
    public void sendToAll(String message, List<String> phones) {

        // 🔴 UI Notification
        messagingTemplate.convertAndSend(
                "/topic/notifications",
                new Notification(message)
        );

        // 🟢 WhatsApp + SMS
        for (String phone : phones) {
            whatsAppService.sendWhatsApp(phone, message);
            smsService.sendSMS(phone, message); // ✅ NEW
        }
    }

    // ✅ SEND TO ONE TRAINER
    public void sendToTrainer(String phone, String message) {

        messagingTemplate.convertAndSend(
                "/topic/notifications",
                new Notification(message)
        );

        whatsAppService.sendWhatsApp(phone, message);
        smsService.sendSMS(phone, message); // ✅ NEW
    }
}