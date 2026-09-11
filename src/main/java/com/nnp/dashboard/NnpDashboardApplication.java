package com.nnp.dashboard;

import org.jspecify.annotations.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Main Spring Boot Entry Point for NNP Dashboard Backend.
 * Enables background scheduling and configures core application beans including CORS and ModelMapper.
 */
@SpringBootApplication
@EnableScheduling
public class NnpDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(NnpDashboardApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer(
            @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://localhost:8080}") List<String> allowedOrigins) {

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigins.toArray(new String[0]))
                        .allowedMethods(
                                "GET",
                                "POST",
                                "PUT",
                                "PATCH",
                                "DELETE",
                                "OPTIONS"
                        )
                        .allowedHeaders("Authorization", "Content-Type")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }

    @Bean(name = "modelMapper")
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }
}