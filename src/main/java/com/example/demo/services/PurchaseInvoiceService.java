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
import com.example.demo.models.PurchaseInvoice;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PurchaseInvoiceService {
    
    public List<PurchaseInvoice> getPurchaseInvoices(String sessionId) throws Exception {
        String fields = "[\"name\", \"supplier\", \"status\", \"total\", \"outstanding_amount\"]";
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Purchase Invoice?fields=" + fields;
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

        ObjectMapper objectMapper = new ObjectMapper();

        List<Map<String, Object>> rawInvoices = (List<Map<String, Object>>) response.getBody().get("data");

        return rawInvoices.stream()
            .map(data -> objectMapper.convertValue(data, PurchaseInvoice.class))
            .toList();   
    }
}
