package com.example.order.application.ports.out;

public interface EmailPort {
    void sendEmail(String to, String subject, String body);
}
