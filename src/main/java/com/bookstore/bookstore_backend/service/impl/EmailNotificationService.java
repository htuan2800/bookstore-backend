package com.bookstore.bookstore_backend.service.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import com.bookstore.bookstore_backend.service.NotificationService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Primary
public class EmailNotificationService implements NotificationService {
    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    @PostConstruct
    public void init() {
        log.info(
                "==> [BEAN LIFECYCLE - @PostConstruct] EmailNotificationService đã được tạo và nạp vào IoC Container!");
    }

    @PreDestroy
    public void cleanup() {
        log.info("==> [BEAN LIFECYCLE - @PreDestroy] EmailNotificationService chuẩn bị bị hủy khi ứng dụng tắt!");
    }

    @Override
    public String send(String to, String message) {
        String result = "Gửi SMS thành công tới: " + to + " với nội dung: " + message;
        log.info(result);
        return result;
    }
}