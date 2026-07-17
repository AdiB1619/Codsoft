package com.yourcompany.currencyconverter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Spring configuration for the HTTP client used to call external APIs.
 *
 * <p>A dedicated {@link RestTemplate} bean (rather than {@code new RestTemplate()})
 * allows connection and read timeouts to be centrally managed and injected
 * consistently across the application.
 *
 * <p>Timeout values are externalised to {@code application.properties} so they can
 * be tuned per environment without recompiling.
 *
 * <p>In the future, this class can also configure:
 * <ul>
 *   <li>Retry interceptors (e.g. using Spring Retry or a custom
 *       {@code ClientHttpRequestInterceptor}).</li>
 *   <li>Request/response logging interceptors.</li>
 *   <li>API key header injection interceptors.</li>
 * </ul>
 */
@Configuration
public class RestTemplateConfig {

    /** Connection timeout in milliseconds (default: 3000 ms = 3 s). */
    @Value("${exchangerate.api.timeout.connect:3000}")
    private int connectTimeoutMs;

    /** Read (socket) timeout in milliseconds (default: 5000 ms = 5 s). */
    @Value("${exchangerate.api.timeout.read:5000}")
    private int readTimeoutMs;

    /**
     * Produces a {@link RestTemplate} with explicit connect and read timeouts.
     *
     * <p>Using {@link RestTemplateBuilder} is the Spring Boot–recommended approach
     * as it auto-applies any registered {@code RestTemplateCustomizer} beans
     * (e.g. from Actuator or custom interceptors).
     *
     * @param builder auto-configured builder provided by Spring Boot
     * @return a ready-to-use {@link RestTemplate}
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(connectTimeoutMs))
                .setReadTimeout(Duration.ofMillis(readTimeoutMs))
                .build();
    }
}
