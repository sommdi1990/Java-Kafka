package com.kafka.cbi.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "external_services")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "service_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Column(name = "endpoint_url", nullable = false)
    private String endpointUrl;

    @Column(name = "wsdl_url")
    private String wsdlUrl;

    @Column(name = "authentication_type")
    @Enumerated(EnumType.STRING)
    private AuthenticationType authenticationType;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "headers", columnDefinition = "TEXT")
    private String headers;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "timeout_ms")
    private Integer timeoutMs;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ServiceType {
        REST_API, SOAP_WEB_SERVICE, GRAPHQL, FTP, EMAIL
    }

    public enum AuthenticationType {
        NONE, BASIC_AUTH, API_KEY, OAUTH2, JWT, CUSTOM
    }
}
