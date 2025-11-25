package com.kafka.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableAdminServer
@ComponentScan(basePackages = {"com.kafka.admin", "com.kafka.shared"})
@EnableJpaRepositories(basePackages = {"com.kafka.shared"})
@EntityScan(basePackages = {"com.kafka.shared"})
public class SpringBootAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAdminApplication.class, args);
    }
}
