package com.example.Training_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


import com.example.Training_system.entity.Notification;

@Service
public class NotificationService {

	  private final SimpMessagingTemplate messagingTemplate;
	    private final WhatsAppService whatsAppService;


	    public NotificationService(SimpMessagingTemplate messagingTemplate,
	                               WhatsAppService whatsAppService) {
	        this.messagingTemplate = messagingTemplate;
	        this.whatsAppService = whatsAppService;
	       
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
           
        }
    }

    // ✅ SEND TO ONE TRAINER
    public void sendToTrainer(String phone, String message) {

        messagingTemplate.convertAndSend(
                "/topic/notifications",
                new Notification(message)
        );

        whatsAppService.sendWhatsApp(phone, message);
        
    }
   
 // ✅ UI ONLY (IMPORTANT)
    public void sendToUI(String message) {

        messagingTemplate.convertAndSend(
                "/topic/notifications",
                new Notification(message)
        );
    }
}