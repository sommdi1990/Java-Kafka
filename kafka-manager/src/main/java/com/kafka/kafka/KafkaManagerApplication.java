package com.kafka.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.kafka.kafka", "com.kafka.shared"})
public class KafkaManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaManagerApplication.class, args);
    }
}
