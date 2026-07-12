package com.codsoft.sms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI Configuration for Swagger UI documentation.
 *
 * <p>Exposes the interactive API documentation at /swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI studentManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Student Management System API")
                        .description("REST API for managing students and courses — CodSoft Java Internship")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("dev@codsoft.example.com")));
    }
}
