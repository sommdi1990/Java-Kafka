package com.kafka.cbi.service;

import com.kafka.cbi.dto.ExternalService;
import com.kafka.shared.annotation.LogExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class WsdlService {

    private final WebServiceTemplate webServiceTemplate;

    @LogExecution
    public String callWebService(ExternalService service, Object request, String soapAction) {
        try {
            String result = (String) webServiceTemplate.marshalSendAndReceive(
                    service.getEndpointUrl(),
                    request,
                    new SoapActionCallback(soapAction)
            );

            log.info("Web service call successful for service: {}", service.getName());
            return result;

        } catch (Exception e) {
            log.error("Web service call failed for service: {}, error: {}", service.getName(), e.getMessage());
            throw new RuntimeException("Web service call failed", e);
        }
    }

    @LogExecution
    public String generateSoapRequest(String operationName, Map<String, Object> parameters) {
        StringBuilder soapRequest = new StringBuilder();
        soapRequest.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        soapRequest.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        soapRequest.append("<soap:Body>");
        soapRequest.append("<").append(operationName).append(">");

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            soapRequest.append("<").append(entry.getKey()).append(">");
            soapRequest.append(entry.getValue());
            soapRequest.append("</").append(entry.getKey()).append(">");
        }

        soapRequest.append("</").append(operationName).append(">");
        soapRequest.append("</soap:Body>");
        soapRequest.append("</soap:Envelope>");

        return soapRequest.toString();
    }
}
