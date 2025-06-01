package com.example.demo.services;

import java.util.ArrayList;
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
import com.example.demo.models.PaymentEntry;
import com.example.demo.models.SupplierQuotation;
import com.example.demo.models.SupplierQuotationItem;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SupplierQuotationService {

    public List<SupplierQuotation> getSupplierQuotationsBySupplier(String supplierName, String sessionId) throws Exception {
        String fields = "[\"name\", \"supplier\", \"status\", \"total\",\"total_qty\"]";
        String filters = "[[\"supplier\", \"=\", \"" + supplierName + "\"]]";
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Supplier Quotation?fields=" + fields + "&filters=" + filters;
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
            .map(item -> objectMapper.convertValue(item, SupplierQuotationItem.class))
            .toList();

        SupplierQuotation quotation = objectMapper.convertValue(rawQuotation, SupplierQuotation.class);
        quotation.setItems(items);

        return quotation;
    }

    public void validateSupplierQuotation(String id, String sessionId) throws Exception {
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Supplier Quotation/" + id + "?run_method=submit";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            Map.class
        );
    }

    public void updateSupplierQuotationItem(String id, String sessionId, SupplierQuotationItem updatedItem) throws Exception {
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Supplier Quotation Item/" + id;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        String updatedItemJson = objectMapper.writeValueAsString(updatedItem);

        HttpEntity<String> request = new HttpEntity<>(updatedItemJson, headers);

        restTemplate.exchange(
            url,
            HttpMethod.PUT,
            request,
            Map.class
        );
    }

    public void updateSupplierQuotationItemRate(String id, String sessionId, double newRate) throws Exception {
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Supplier Quotation Item/" + id;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> updateFields = Map.of("rate", newRate);
        ObjectMapper objectMapper = new ObjectMapper();
        String updateFieldsJson = objectMapper.writeValueAsString(updateFields);

        HttpEntity<String> request = new HttpEntity<>(updateFieldsJson, headers);

        restTemplate.exchange(
            url,
            HttpMethod.PUT,
            request,
            Map.class
        );
    }

    public SupplierQuotation createSupplierQuotation(String sessionId, SupplierQuotation supplierQuotation) throws Exception{
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Supplier Quotation";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));        

        HttpEntity<SupplierQuotation> request = new HttpEntity<>(supplierQuotation, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            Map.class
        );

        Map<String, Object> responseData = (Map<String, Object>) response.getBody().get("data");
        return new ObjectMapper().convertValue(responseData, SupplierQuotation.class); 
    }

    public void setSupplierQuotationItems(SupplierQuotation supplierQuotation, Map<String, String> params, int itemCount){
        List<SupplierQuotationItem> items = new ArrayList<>();
        for (int i = 1; i <= itemCount; i++) {
            if (params.get("item_" + i) != null) {
                SupplierQuotationItem item = new SupplierQuotationItem();
                item.setItemCode(params.get("item_" + i));
                item.setWarehouse(params.get("warehouse_" + i));
                item.setQty(Double.parseDouble(params.get("qty_" + i)));
                item.setRate(Double.parseDouble(params.get("rate_" + i)));
                items.add(item);
            }
        }
        supplierQuotation.setItems(items);
    }
    
}
