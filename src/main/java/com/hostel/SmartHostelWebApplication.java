package com.hostel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.hostel")
@EnableJpaRepositories(basePackages = "com.hostel.repository")
@EntityScan(basePackages = "com.hostel.model")
public class SmartHostelWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartHostelWebApplication.class, args);
    }
}
    
