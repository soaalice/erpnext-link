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
import com.example.demo.models.SupplierQuotation;
import com.example.demo.models.SupplierQuotationItem;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SupplierQuotationService {

    public List<SupplierQuotation> getSupplierQuotationsBySupplier(String supplierName, String sessionId) throws Exception {
        String fields = "[\"name\", \"supplier\", \"status\"]";
        String filters = "[[\"supplier\", \"=\", \"" + supplierName + "\"]]";
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Supplier Quotation?fields=" + fields + "&filters=" + filters;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionId);
        headers.setContentType(MediaType.APPLICATION_JSON);


        HttpEntity<String> request = new HttpEntity<>(headers);

        System.out.println("Request: " + request);

        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            Map.class
        );

        System.out.println("Response: " + response.getBody());

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> rawQuotations = (List<Map<String, Object>>) response.getBody().get("data");

        return rawQuotations.stream()
            .map(data -> objectMapper.convertValue(data, SupplierQuotation.class))
            .toList();
    }

    public SupplierQuotation fetchSupplierQuotationById(String id, String sessionId) throws Exception {
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Supplier Quotation/" + id;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            Map.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> rawQuotation = (Map<String, Object>) response.getBody().get("data");

        List<Map<String, Object>> rawItems = (List<Map<String, Object>>) rawQuotation.get("items");
        List<SupplierQuotationItem> items = rawItems.stream()
        .map(item -> {
            try {
                String json = objectMapper.writeValueAsString(item);
                return objectMapper.readValue(json, SupplierQuotationItem.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        })
        .toList();

        SupplierQuotation quotation = objectMapper.convertValue(rawQuotation, SupplierQuotation.class);
        quotation.setItems(items);

        return quotation;
    }
    
}
