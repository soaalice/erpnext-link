package com.example.demo.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.config.ErpApiConfig;
import com.example.demo.models.RequestQuotation;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RequestQuotationService {

    public List<RequestQuotation> getRequestQuotationsBySupplier(String supplierId, String sessionId) throws Exception {
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Request for Quotation?supplier=" + supplierId;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionId);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            Map.class
        );

        System.out.println("Response: " + response.getBody());

        ObjectMapper objectMapper = new ObjectMapper();

        List<Map<String, Object>> rawSuppliers = (List<Map<String, Object>>) response.getBody().get("data");

        return rawSuppliers.stream()
            .map(data -> objectMapper.convertValue(data, RequestQuotation.class))
            .toList();
    }
}
