package com.codsoft.sms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA auditing configuration.
 *
 * <p>Enables Spring Data JPA's auditing infrastructure, which automatically
 * populates {@code @CreatedDate} and {@code @LastModifiedDate} fields on any
 * entity annotated with {@code @EntityListeners(AuditingEntityListener.class)}.
 *
 * <p>This configuration class owns the {@code @EnableJpaAuditing} annotation
 * so that it is clearly separated from the application bootstrap class
 * ({@link com.codsoft.sms.SmsApplication}), following the single-responsibility
 * principle for Spring configuration classes.
 *
 * <p>No {@code AuditorAware} bean is registered here because v1.0 has no
 * authentication layer. When authentication is added (Section 20 of the SDD),
 * this class should be extended to provide an {@code AuditorAware<String>} bean
 * that resolves the current user from the Security context.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // No beans required in v1.0 — @EnableJpaAuditing is the sole responsibility
    // of this configuration class.
}
