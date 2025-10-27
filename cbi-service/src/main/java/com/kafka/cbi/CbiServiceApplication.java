package com.kafka.cbi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.kafka.cbi", "com.kafka.shared"})
public class CbiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CbiServiceApplication.class, args);
    }
}
