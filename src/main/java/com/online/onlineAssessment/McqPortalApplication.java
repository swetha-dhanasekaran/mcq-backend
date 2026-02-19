package com.online.onlineAssessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Main Application Class
 * AI-Powered Topic-Based MCQ Interview Preparation Portal
 */
@SpringBootApplication
public class McqPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(McqPortalApplication.class, args);
        System.out.println("=".repeat(60));
        System.out.println("MCQ Interview Portal Started Successfully!");
        System.out.println("=".repeat(60));
    }

    /**
     * RestTemplate bean for HTTP requests
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
