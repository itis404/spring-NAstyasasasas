package com.example.tripsplit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TripsplitApplication {
    public static void main(String[] args) {
        SpringApplication.run(TripsplitApplication.class, args);
    }
}
