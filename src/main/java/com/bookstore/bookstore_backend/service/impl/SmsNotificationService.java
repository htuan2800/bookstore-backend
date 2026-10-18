package com.bookstore.bookstore_backend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.bookstore.bookstore_backend.service.NotificationService;

@Service("smsNotificationService")
public class SmsNotificationService implements NotificationService {
    private static final Logger log = LoggerFactory.getLogger(SmsNotificationService.class);
    @Override
    public String send(String to, String message) {
        String result = "Gửi SMS thành công tới: " + to + " với nội dung: " + message;
        log.info(result);
        return result;
    }
}