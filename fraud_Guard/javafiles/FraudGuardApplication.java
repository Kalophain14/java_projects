package com.fraudguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Main Spring Boot Application entry point for FraudGuard Academy
 * Learn-to-Earn fraud prevention platform
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class FraudGuardApplication {

    public static void main(String[] args) {
        SpringApplication.run(FraudGuardApplication.class, args);
    }

    /**
     * Configure OpenAPI/Swagger documentation
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FraudGuard Academy API")
                        .version("1.0.0")
                        .description("RESTful API for the FraudGuard Learn-to-Earn fraud prevention platform. " +
                                "Educate users on fraud detection while earning airtime rewards.")
                );
    }
}
