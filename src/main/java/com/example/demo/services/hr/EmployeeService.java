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
import com.example.demo.models.hr.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmployeeService {
    public List<Employee> getEmployees(String sessionId) throws Exception {
        String fields = "[\"name\", \"gender\", \"company\", \"employee_name\", \"ctc\"]";
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Employee?fields=" + fields;
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
            .map(data -> objectMapper.convertValue(data, Employee.class))
            .toList();   
    }

    public Employee getEmployee(String sessionId, String name) throws Exception {
        String url = ErpApiConfig.ERP_URL_RESOURCE + "/Employee/" + name;
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
        Map<String, Object> rawEmployee = (Map<String, Object>) response.getBody().get("data");

        // List<Map<String, Object>> rawItems = (List<Map<String, Object>>) rawEmployee.get("items");
        // List<PurchaseOrderItem> items = rawItems.stream()
        //     .map(item -> objectMapper.convertValue(item, PurchaseOrderItem.class))
        //     .toList();

        Employee employee = objectMapper.convertValue(rawEmployee, Employee.class);
        // employee.setItems(items);

        return employee;
    }


}
