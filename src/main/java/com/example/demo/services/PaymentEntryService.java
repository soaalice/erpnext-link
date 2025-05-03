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
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PaymentEntryService {
    
    public PaymentEntry createPaymentEntry(String sessionId, PaymentEntry paymentEntry, String purchaseInvoice) throws Exception {
        paymentEntry.setReferences(List.of(generateReference(purchaseInvoice, paymentEntry)));
        paymentEntry.setReceivedAmount(paymentEntry.getPaidAmount());

        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Payment Entry";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<PaymentEntry> request = new HttpEntity<>(paymentEntry, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            Map.class
        );

        Map<String, Object> responseData = (Map<String, Object>) response.getBody().get("data");
        return new ObjectMapper().convertValue(responseData, PaymentEntry.class);
    }

    public void validatePaymentEntry(String sessionId, PaymentEntry paymentEntry) throws Exception {
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Payment Entry/" + paymentEntry.getName() + "?run_method=submit";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            Void.class
        );
    }

    public void savePaymentEntry(String sessionId, PaymentEntry paymentEntry, String purchaseInvoice) throws Exception{
        paymentEntry = createPaymentEntry(sessionId, paymentEntry, purchaseInvoice);
        validatePaymentEntry(sessionId, paymentEntry);
    }

    private Map<String, Object> generateReference (String referenceName, PaymentEntry paymentEntry) {
        return Map.of(
            "reference_name", referenceName,
            "reference_doctype", "Purchase Invoice",
            "total_amount", paymentEntry.getTotalAmount(),
            "allocated_amount", paymentEntry.getPaidAmount(),
            "parenttype", "Payment Entry",
            "parentfield", "references"
        );
    }
}
