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
import com.example.demo.models.SupplierQuotationItem;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SupplierQuotationItemService {

    public SupplierQuotationItem updateSupplierQuotationItem(String id, String sessionId, SupplierQuotationItem updatedItem) throws Exception {
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Supplier Quotation Item/" + id;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        String updatedItemJson = objectMapper.writeValueAsString(updatedItem);

        HttpEntity<String> request = new HttpEntity<>(updatedItemJson, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.PUT,
            request,
            Map.class
        );

        Map<String, Object> rawItem = (Map<String, Object>) response.getBody().get("data");
        return objectMapper.convertValue(rawItem, SupplierQuotationItem.class);
    }

    public SupplierQuotationItem updateSupplierQuotationItemRate(String id, String sessionId, double newRate) throws Exception {
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Supplier Quotation Item/" + id;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> updateFields = Map.of("rate", newRate);
        ObjectMapper objectMapper = new ObjectMapper();
        String updateFieldsJson = objectMapper.writeValueAsString(updateFields);

        HttpEntity<String> request = new HttpEntity<>(updateFieldsJson, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.PUT,
            request,
            Map.class
        );

        Map<String, Object> rawItem = (Map<String, Object>) response.getBody().get("data");
        return objectMapper.convertValue(rawItem, SupplierQuotationItem.class);
    }
}
