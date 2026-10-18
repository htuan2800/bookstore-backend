package com.bookstore.bookstore_backend.service;

public interface NotificationService {
    String send(String to, String message);
}