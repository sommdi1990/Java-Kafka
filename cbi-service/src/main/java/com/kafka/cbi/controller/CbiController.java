package com.kafka.cbi.controller;

import com.kafka.cbi.dto.ExternalService;
import com.kafka.cbi.service.ExternalApiService;
import com.kafka.cbi.service.WsdlService;
import com.kafka.shared.annotation.LogExecution;
import com.kafka.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cbi")
@RequiredArgsConstructor
public class CbiController {

    private final ExternalApiService externalApiService;
    private final WsdlService wsdlService;

    @PostMapping("/rest/{serviceId}")
    @LogExecution
    public ApiResponse<String> callRestApi(
            @PathVariable Long serviceId,
            @RequestParam String endpoint,
            @RequestBody Map<String, Object> requestBody) {

        // In a real implementation, you would fetch the service from database
        ExternalService service = createSampleService(serviceId);

        String result = externalApiService.callRestApi(service, endpoint, requestBody).block();
        return ApiResponse.success("REST API call completed", result);
    }

    @PostMapping("/soap/{serviceId}")
    @LogExecution
    public ApiResponse<String> callSoapService(
            @PathVariable Long serviceId,
            @RequestParam String soapAction,
            @RequestBody String soapBody) {

        ExternalService service = createSampleService(serviceId);

        String result = externalApiService.callSoapWebService(service, soapAction, soapBody).block();
        return ApiResponse.success("SOAP service call completed", result);
    }

    @PostMapping("/graphql/{serviceId}")
    @LogExecution
    public ApiResponse<String> callGraphQLApi(
            @PathVariable Long serviceId,
            @RequestParam String query,
            @RequestBody(required = false) Map<String, Object> variables) {

        ExternalService service = createSampleService(serviceId);

        String result = externalApiService.callGraphQLApi(service, query, variables).block();
        return ApiResponse.success("GraphQL API call completed", result);
    }

    @PostMapping("/wsdl/{serviceId}")
    @LogExecution
    public ApiResponse<String> callWsdlService(
            @PathVariable Long serviceId,
            @RequestParam String operationName,
            @RequestBody Map<String, Object> parameters) {

        ExternalService service = createSampleService(serviceId);
        String soapRequest = wsdlService.generateSoapRequest(operationName, parameters);

        String result = wsdlService.callWebService(service, soapRequest, operationName);
        return ApiResponse.success("WSDL service call completed", result);
    }

    private ExternalService createSampleService(Long serviceId) {
        return ExternalService.builder()
                .id(serviceId)
                .name("Sample Service " + serviceId)
                .serviceType(ExternalService.ServiceType.REST_API)
                .endpointUrl("https://jsonplaceholder.typicode.com")
                .isActive(true)
                .timeoutMs(30000)
                .retryCount(3)
                .build();
    }
}
