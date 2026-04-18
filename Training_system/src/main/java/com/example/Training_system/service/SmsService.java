package com.example.Training_system.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.sms.from}")
    private String from;

    public void sendSMS(String mobile, String message) {
        try {
            String to = formatNumber(mobile);

            System.out.println("📤 Sending SMS to: " + to);

            Message msg = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(from),
                    message
            ).create();

            System.out.println("✅ SMS SID: " + msg.getSid());

        } catch (Exception e) {
            System.err.println("❌ SMS FAILED: " + e.getMessage());
        }
    }

    private String formatNumber(String mobile) {

        mobile = mobile.replaceAll("\\s+", "");

        if (mobile.startsWith("+91")) return mobile;
        if (mobile.startsWith("91")) return "+" + mobile;
        if (mobile.length() == 10) return "+91" + mobile;

        throw new RuntimeException("Invalid number: " + mobile);
    }
}