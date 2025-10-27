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
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalApiService {

    private final WebClient.Builder webClientBuilder;

    @LogExecution
    public Mono<String> callRestApi(ExternalService service, String endpoint, Map<String, Object> requestBody) {
        WebClient webClient = webClientBuilder
                .baseUrl(service.getEndpointUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        return webClient.post()
                .uri(endpoint)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(service.getTimeoutMs() != null ? service.getTimeoutMs() : 30000))
                .doOnSuccess(response -> log.info("API call successful for service: {}", service.getName()))
                .doOnError(error -> log.error("API call failed for service: {}, error: {}", service.getName(), error.getMessage()));
    }

    @LogExecution
    public Mono<String> callSoapWebService(ExternalService service, String soapAction, String soapBody) {
        WebClient webClient = webClientBuilder
                .baseUrl(service.getEndpointUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "text/xml; charset=utf-8")
                .defaultHeader("SOAPAction", soapAction)
                .build();

        return webClient.post()
                .bodyValue(soapBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(service.getTimeoutMs() != null ? service.getTimeoutMs() : 30000))
                .doOnSuccess(response -> log.info("SOAP call successful for service: {}", service.getName()))
                .doOnError(error -> log.error("SOAP call failed for service: {}, error: {}", service.getName(), error.getMessage()));
    }

    @LogExecution
    public Mono<String> callGraphQLApi(ExternalService service, String query, Map<String, Object> variables) {
        WebClient webClient = webClientBuilder
                .baseUrl(service.getEndpointUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        Map<String, Object> requestBody = Map.of(
                "query", query,
                "variables", variables != null ? variables : Map.of()
        );

        return webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(service.getTimeoutMs() != null ? service.getTimeoutMs() : 30000))
                .doOnSuccess(response -> log.info("GraphQL call successful for service: {}", service.getName()))
                .doOnError(error -> log.error("GraphQL call failed for service: {}, error: {}", service.getName(), error.getMessage()));
    }
}
