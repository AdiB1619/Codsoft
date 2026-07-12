package com.codsoft.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Student Management System Spring Boot application.
 *
 * <p>JPA auditing ({@code @CreatedDate} / {@code @LastModifiedDate}) is enabled
 * via {@link com.codsoft.sms.config.JpaAuditingConfig} — not here — to keep this
 * class focused solely on application bootstrap.
 */
@SpringBootApplication
public class SmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsApplication.class, args);
    }
}
