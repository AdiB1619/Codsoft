package com.yourcompany.currencyconverter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Currency Converter Spring Boot application.
 *
 * <p>The {@link SpringBootApplication} annotation is a convenience meta-annotation
 * that combines:
 * <ul>
 *   <li>{@code @Configuration}        – marks this class as a source of bean definitions.</li>
 *   <li>{@code @EnableAutoConfiguration} – tells Spring Boot to auto-configure the
 *       application context based on classpath dependencies.</li>
 *   <li>{@code @ComponentScan}         – scans this package (and sub-packages) for Spring
 *       components, services, controllers, and repositories.</li>
 * </ul>
 *
 * <p>Running this class starts an embedded Tomcat server on port {@code 8080} by default.
 * The {@code /actuator/health} endpoint (provided by {@code spring-boot-starter-actuator})
 * should return {@code {"status":"UP"}} once the application has fully started.
 *
 * <h2>Bean Validation (JSR-303 / Jakarta)</h2>
 * <p>The {@code spring-boot-starter-validation} dependency is on the classpath, which
 * auto-configures the Hibernate Validator implementation. Controllers can apply
 * {@code @Valid} on {@code @RequestBody} parameters to trigger validation of
 * {@link com.yourcompany.currencyconverter.model.dto.ConversionRequest} and other DTOs.
 * Validation failures throw {@code MethodArgumentNotValidException}, which the
 * {@code GlobalExceptionHandler} maps to HTTP 400 with field-level error detail.
 */
@SpringBootApplication
public class CurrencyConverterApplication {

    /**
     * Application entry point.
     *
     * @param args command-line arguments forwarded to the Spring context
     */
    public static void main(String[] args) {
        SpringApplication.run(CurrencyConverterApplication.class, args);
    }
}
