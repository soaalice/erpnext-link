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
import com.example.demo.models.Warehouse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WarehouseService {
    
    public List<Warehouse> getWarehouses(String sessionId) throws Exception{
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Warehouse";
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
        List<Map<String, Object>> rawQuotations = (List<Map<String, Object>>) response.getBody().get("data");

        return rawQuotations.stream()
            .map(data -> objectMapper.convertValue(data, Warehouse.class))
            .toList();
    }
}
