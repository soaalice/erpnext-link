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
import com.example.demo.models.PurchaseOrder;
import com.example.demo.models.PurchaseOrderItem;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PurchaseOrderService {
    
    public List<PurchaseOrder> getPurchaseOrdersBySupplier(String supplierId, String sessionId) throws Exception {
        String fields = "[\"name\", \"supplier\", \"status\", \"advance_payment_status\", \"total\"]";
        String filters = "[[\"supplier\", \"=\", \"" + supplierId + "\"]]";
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Purchase Order?fields=" + fields + "&filters=" + filters;
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

        List<Map<String, Object>> rawSuppliers = (List<Map<String, Object>>) response.getBody().get("data");

        return rawSuppliers.stream()
            .map(data -> objectMapper.convertValue(data, PurchaseOrder.class))
            .toList();
    }

    public List<PurchaseOrder> getPurchaseOrdersByStatus(String status, String sessionId) throws Exception {
        String fields = "[\"name\", \"supplier\", \"status\", \"advance_payment_status\", \"total\"]";
        String filters = "[[\"status\", \"=\", \"" + status + "\"]]";
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Purchase Order?fields=" + fields + "&filters=" + filters;
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

        List<Map<String, Object>> rawSuppliers = (List<Map<String, Object>>) response.getBody().get("data");

        return rawSuppliers.stream()
            .map(data -> objectMapper.convertValue(data, PurchaseOrder.class))
            .toList();
    }

    public List<PurchaseOrder> getPurchaseOrdersByStatuses(String[] statuses, String sessionId) throws Exception {
        String fields = "[\"name\", \"supplier\", \"status\", \"advance_payment_status\", \"total\"]";

        StringBuilder filtersBuilder = new StringBuilder("[");
        for (int i = 0; i < statuses.length; i++) {
            filtersBuilder.append("[\"status\", \"=\", \"").append(statuses[i]).append("\"]");
            if (i < statuses.length - 1) {
                filtersBuilder.append(", \"or\", ");
            }
        }
        filtersBuilder.append("]");

        String filters = filtersBuilder.toString();
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Purchase Order?fields=" + fields + "&filters=" + filters;

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

        List<Map<String, Object>> rawSuppliers = (List<Map<String, Object>>) response.getBody().get("data");

        return rawSuppliers.stream()
            .map(data -> objectMapper.convertValue(data, PurchaseOrder.class))
            .toList();
    }

    public PurchaseOrder getPurchaseOrder(String sessionId, String name) throws Exception {
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Purchase Order/" + name;
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
        Map<String, Object> rawPurchaseOrder = (Map<String, Object>) response.getBody().get("data");

        List<Map<String, Object>> rawItems = (List<Map<String, Object>>) rawPurchaseOrder.get("items");
        List<PurchaseOrderItem> items = rawItems.stream()
            .map(item -> objectMapper.convertValue(item, PurchaseOrderItem.class))
            .toList();

        PurchaseOrder purchaseOrder = objectMapper.convertValue(rawPurchaseOrder, PurchaseOrder.class);
        purchaseOrder.setItems(items);

        return purchaseOrder;
    }

}
