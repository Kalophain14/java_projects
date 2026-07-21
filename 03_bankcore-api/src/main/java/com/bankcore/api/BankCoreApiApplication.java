package com.bankcore.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BankCoreApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankCoreApiApplication.class, args);
    }
}