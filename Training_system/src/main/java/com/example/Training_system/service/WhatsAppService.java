package com.example.Training_system.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {

    @Value("${twilio.whatsapp.from}")
    private String from;

    public void sendWhatsApp(String mobile, String message) {

        try {
            String to = formatNumber(mobile);

            System.out.println("📤 Sending WhatsApp to: " + to);

            Message msg = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(from),
                    message
            ).create();

            System.out.println("✅ SID: " + msg.getSid());
            System.out.println("📊 STATUS: " + msg.getStatus());

        } catch (Exception e) {
            System.err.println("❌ WhatsApp FAILED: " + e.getMessage());
        }
    }

    private String formatNumber(String mobile) {

        mobile = mobile.replaceAll("\\s+", "");

        if (mobile.startsWith("whatsapp:")) return mobile;

        if (mobile.startsWith("+91")) return "whatsapp:" + mobile;

        if (mobile.startsWith("91")) return "whatsapp:+" + mobile;

        if (mobile.length() == 10) return "whatsapp:+91" + mobile;

        throw new RuntimeException("Invalid number: " + mobile);
    }
}