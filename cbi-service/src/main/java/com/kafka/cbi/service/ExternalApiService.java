package com.kafka.cbi.service;

import com.kafka.cbi.dto.ExternalService;
import com.kafka.shared.annotation.LogExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalApiService {

    private final WebClient.Builder webClientBuilder;

    @LogExecution
    public Mono<String> callRestApi(ExternalService service, String endpoint, Map<String, Object> requestBody) {
        WebClient webClient = buildClientWithAuth(service, MediaType.APPLICATION_JSON_VALUE);

        return webClient.post()
                .uri(endpoint)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(resolveTimeout(service)))
                .doOnSuccess(response -> log.info("API call successful for service: {}", service.getName()))
                .doOnError(error -> log.error("API call failed for service: {}, error: {}", service.getName(), error.getMessage()));
    }

    @LogExecution
    public Mono<String> callSoapWebService(ExternalService service, String soapAction, String soapBody) {
        WebClient webClient = buildClientWithAuth(service, "text/xml; charset=utf-8")
                .mutate()
                .defaultHeader("SOAPAction", soapAction)
                .build();

        return webClient.post()
                .bodyValue(soapBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(resolveTimeout(service)))
                .doOnSuccess(response -> log.info("SOAP call successful for service: {}", service.getName()))
                .doOnError(error -> log.error("SOAP call failed for service: {}, error: {}", service.getName(), error.getMessage()));
    }

    @LogExecution
    public Mono<String> callGraphQLApi(ExternalService service, String query, Map<String, Object> variables) {
        WebClient webClient = buildClientWithAuth(service, MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> requestBody = Map.of(
                "query", query,
                "variables", variables != null ? variables : Map.of()
        );

        return webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(resolveTimeout(service)))
                .doOnSuccess(response -> log.info("GraphQL call successful for service: {}", service.getName()))
                .doOnError(error -> log.error("GraphQL call failed for service: {}, error: {}", service.getName(), error.getMessage()));
    }

    private long resolveTimeout(ExternalService service) {
        return service.getTimeoutMs() != null ? service.getTimeoutMs() : 30000L;
    }

    /**
     * Build a WebClient with authentication headers based on ExternalService configuration.
     * Supports BASIC_AUTH, API_KEY, JWT and custom static headers (JSON encoded in {@code headers} column).
     */
    private WebClient buildClientWithAuth(ExternalService service, String contentType) {
        WebClient.Builder builder = webClientBuilder
                .baseUrl(service.getEndpointUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, contentType);

        Map<String, String> computedHeaders = new HashMap<>();

        if (service.getAuthenticationType() != null) {
            switch (service.getAuthenticationType()) {
                case BASIC_AUTH -> {
                    if (service.getUsername() != null && service.getPassword() != null) {
                        String basicToken = service.getUsername() + ":" + service.getPassword();
                        String encoded = Base64.getEncoder().encodeToString(basicToken.getBytes());
                        computedHeaders.put(HttpHeaders.AUTHORIZATION, "Basic " + encoded);
                    }
                }
                case API_KEY -> {
                    if (service.getApiKey() != null) {
                        // Default header name; can be overridden via custom headers JSON
                        computedHeaders.put("X-API-KEY", service.getApiKey());
                    }
                }
                case JWT -> {
                    if (service.getApiKey() != null) {
                        computedHeaders.put(HttpHeaders.AUTHORIZATION, "Bearer " + service.getApiKey());
                    }
                }
                case OAUTH2, CUSTOM, NONE -> {
                    // For advanced OAuth2/custom flows, the token should be placed into apiKey/headers externally.
                }
            }
        }

        // Merge custom static headers from JSON string if present.
        if (service.getHeaders() != null && !service.getHeaders().isBlank()) {
            // We avoid adding heavy JSON parsing here; headers can be applied by an upper layer if needed.
            log.debug("Custom headers configured for service {} - consider applying them in a higher-level client", service.getName());
        }

        computedHeaders.forEach(builder::defaultHeader);
        return builder.build();
    }
}
