package com.bookstore.bookstore_backend;

import com.bookstore.bookstore_backend.service.BookService;
import com.bookstore.bookstore_backend.service.PrototypeScopeDemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ScopeVerificationRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ScopeVerificationRunner.class);

    // Tiêm 2 biến trỏ vào Singleton Bean (BookService)
    private final BookService singleton1;
    private final BookService singleton2;

    // Tiêm 2 biến trỏ vào Prototype Bean
    private final PrototypeScopeDemo prototype1;
    private final PrototypeScopeDemo prototype2;

    public ScopeVerificationRunner(BookService singleton1, 
                                   BookService singleton2,
                                   PrototypeScopeDemo prototype1, 
                                   PrototypeScopeDemo prototype2) {
        this.singleton1 = singleton1;
        this.singleton2 = singleton2;
        this.prototype1 = prototype1;
        this.prototype2 = prototype2;
    }

    @Override
    public void run(String... args) {
        log.info("========== KIỂM CHỨNG SCOPE CỦA SPRING BEAN ==========");
        log.info("[SINGLETON] Instance 1 HashCode: {}", System.identityHashCode(singleton1));
        log.info("[SINGLETON] Instance 2 HashCode: {}", System.identityHashCode(singleton2));
        log.info("[PROTOTYPE] Instance 1 HashCode: {}", System.identityHashCode(prototype1));
        log.info("[PROTOTYPE] Instance 2 HashCode: {}", System.identityHashCode(prototype2));
        log.info("======================================================");
    }
}