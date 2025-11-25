package com.kafka.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.kafka.kafka", "com.kafka.shared"})
@EnableJpaRepositories(basePackages = {"com.kafka.shared"})
@EntityScan(basePackages = {"com.kafka.shared"})
public class KafkaManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaManagerApplication.class, args);
    }
}
