package com.bookstore.bookstore_backend.service.impl;

import org.springframework.stereotype.Service;
import com.bookstore.bookstore_backend.service.NotificationService;

@Service("smsNotificationService")
public class SmsNotificationService implements NotificationService {
    @Override
    public String send(String to, String message) {
        return "Gửi SMS thành công tới: " + to + " với nội dung: " + message;
    }
}