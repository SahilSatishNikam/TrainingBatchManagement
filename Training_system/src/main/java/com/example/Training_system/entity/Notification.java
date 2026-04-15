package com.example.Training_system.entity;

import java.time.LocalDateTime;

public class Notification {

    private String message;
    private LocalDateTime time;

    public Notification() {}

    public Notification(String message) {
        this.message = message;
        this.time = LocalDateTime.now(); // ✅ auto timestamp
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTime() {
        return time;
    }
}