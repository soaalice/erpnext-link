package com.example.demo.services.hr;

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
import com.example.demo.models.hr.SalaryComponent;
import com.example.demo.models.hr.SalarySlip;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SalarySlipService {
    public List<SalarySlip> getSalarySlips(String sessionId) throws Exception {
        String fields = "[\"*\"]";
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Salary Slip?fields=" + fields;
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

        List<Map<String, Object>> rawSalarySlips = (List<Map<String, Object>>) response.getBody().get("data");

        return rawSalarySlips.stream()
            .map(data -> objectMapper.convertValue(data, SalarySlip.class))
            .toList();
    }

    public List<SalarySlip> getSalarySlipsByEmployee(String employee, String sessionId) throws Exception {
        String fields = "[\"*\"]";
        String filters = "[[\"employee\", \"=\", \"" + employee + "\"]]";
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Salary Slip?fields=" + fields + "&filters=" + filters;
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

        List<Map<String, Object>> rawSalarySlips = (List<Map<String, Object>>) response.getBody().get("data");

        return rawSalarySlips.stream()
            .map(data -> objectMapper.convertValue(data, SalarySlip.class))
            .toList();
    }

    public SalarySlip getSalarySlipByName(String name, String sessionId) throws Exception {
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Salary Slip/" + name;
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
        Map<String, Object> rawSalarySlip = (Map<String, Object>) response.getBody().get("data");

        // Get and convert earnings
        List<Map<String, Object>> rawEarnings = (List<Map<String, Object>>) rawSalarySlip.get("earnings");
        List<SalaryComponent> earnings = rawEarnings.stream()
            .map(item -> objectMapper.convertValue(item, SalaryComponent.class))
            .toList();

        // Get and convert deductions
        List<Map<String, Object>> rawDeductions = (List<Map<String, Object>>) rawSalarySlip.get("deductions");
        List<SalaryComponent> deductions = rawDeductions.stream()
            .map(item -> objectMapper.convertValue(item, SalaryComponent.class))
            .toList();

        SalarySlip salarySlip = objectMapper.convertValue(rawSalarySlip, SalarySlip.class);
        salarySlip.setEarnings(earnings);
        salarySlip.setDeductions(deductions);

        return salarySlip;
    }
}
