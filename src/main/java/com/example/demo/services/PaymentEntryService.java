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
import com.example.demo.models.PaymentEntry;

@Service
public class PaymentEntryService {
    
    public void createPaymentEntry(String sessionId, PaymentEntry paymentEntry) throws Exception {
        paymentEntry.setReceivedAmount(paymentEntry.getTotalAmount() - paymentEntry.getPaidAmount());

        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Payment Entry";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<PaymentEntry> request = new HttpEntity<>(paymentEntry, headers);

        restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            Map.class
        );
    }
}
