package com.example.demo.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.config.ErpApiConfig;
import com.example.demo.models.hr.*;
import com.example.demo.utils.ValueController;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

@Service
public class ImportService {
    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);
    private static final int MAX_RECORDS = 1000;
    private final Map<String, Object> importCache = new ConcurrentHashMap<>();

    public static class ImportException extends RuntimeException {
        public ImportException(String message) {
            super(message);
        }

        public ImportException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public boolean import_data(MultipartFile employee_file, MultipartFile structure_file, 
                             MultipartFile salary_file, HttpSession session) throws Exception {
        clearImportCache();
        String sessionId = validateSession(session);
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();

        try {
            // Parsing et validation des employés
            List<EmployeeDto> employees = parseEmployees(employee_file);
            logger.info("Parsed {} employees", employees.size());

            // Créer une map des employés par référence pour un accès rapide
            Map<Integer, EmployeeDto> employeeMap = new HashMap<>();
            for (EmployeeDto emp : employees) {
                employeeMap.put(emp.getRef(), emp);
            }
            importCache.put("employee_map", employeeMap);

            // Stocker les références d'employés pour la validation
            Set<Integer> employeeRefs = new HashSet<>(employeeMap.keySet());
            importCache.put("employee_refs", employeeRefs);

            // Parsing et validation des structures salariales
            Map<String, SalaryStructure> structureMap = parseStructures(structure_file);
            List<SalaryStructure> structures = new ArrayList<>(structureMap.values());
            logger.info("Parsed {} salary structures", structures.size());

            // Parsing et validation des assignations
            List<SalaryStructureAssignment> assignments = parseAssignments(salary_file, 
                employeeRefs,
                structureMap.keySet());
            logger.info("Parsed {} salary assignments", assignments.size());

            // Préparation de la requête
            String url = ErpApiConfig.ERP_URL_METHOD + "/erpnext.custom_import.api.import_data";
            MultiValueMap<String, String> requestData = new LinkedMultiValueMap<>();
            requestData.add("employees_json", objectMapper.writeValueAsString(employees));
            requestData.add("structure_json", objectMapper.writeValueAsString(structures));
            requestData.add("salary_json", objectMapper.writeValueAsString(assignments));

            // Configuration des headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", sessionId);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestData, headers);

            // Appel API
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Import failed with status: {}", response.getStatusCode());
                throw new ImportException("L'importation a échoué avec le code : " + response.getStatusCode());
            }

            String responseBody = response.getBody();
            if (responseBody == null) {
                logger.error("Empty response from ERPNext");
                throw new ImportException("Réponse vide de ERPNext");
            }

            // Parse la réponse JSON
            Map<String, Object> jsonResponse = objectMapper.readValue(responseBody, Map.class);
            
            // Vérifie si la réponse contient une erreur
            if (jsonResponse.containsKey("exc_type") || jsonResponse.containsKey("exception") || 
                jsonResponse.containsKey("_error_message") || jsonResponse.containsKey("error")) {
                String errorMessage = jsonResponse.containsKey("exception") ? 
                    (String) jsonResponse.get("exception") : 
                    jsonResponse.containsKey("_error_message") ? 
                        (String) jsonResponse.get("_error_message") : 
                        "Erreur inconnue lors de l'importation";
                logger.error("ERPNext error: {}", errorMessage);
                throw new ImportException("Erreur ERPNext : " + errorMessage);
            }

            // Vérifie si l'importation a réussi
            if (jsonResponse.containsKey("message") && "OK".equals(jsonResponse.get("message"))) {
                logger.info("Import successful");
                return true;
            } else {
                logger.error("Unexpected response from ERPNext: {}", responseBody);
                throw new ImportException("Réponse inattendue de ERPNext: " + responseBody);
            }

        } catch (Exception e) {
            logger.error("Import failed", e);
            throw new ImportException("Erreur lors de l'importation: " + e.getMessage(), e);
        } finally {
            clearImportCache();
        }
    }

    private List<EmployeeDto> parseEmployees(MultipartFile file) throws Exception {
        List<EmployeeDto> employees = new ArrayList<>();
        Set<Integer> refs = new HashSet<>();
        importCache.put("employee_refs", refs);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line = reader.readLine(); // Skip header
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (employees.size() >= MAX_RECORDS) {
                    throw new ImportException("Nombre maximum d'employés dépassé (" + MAX_RECORDS + ")");
                }

                String[] columns = line.split(",");
                validateEmployeeColumns(columns, lineNumber);

                EmployeeDto employee = new EmployeeDto();
                try {
                    int ref = Integer.parseInt(columns[0].trim());
                    if (!refs.add(ref)) {
                        throw new ImportException("Référence employé en doublon: " + ref);
                    }
                    employee.setRef(ref);
                    employee.setLastName(columns[1].trim());
                    employee.setFirstName(columns[2].trim());
                    employee.setGender(columns[3].trim());
                    employee.setHireDate(ValueController.validateDate(columns[4].trim()));
                    employee.setDateOfBirth(ValueController.validateDate(columns[5].trim()));
                    employee.setCompany(columns[6].trim());
                    employee.setName(employee.getFirstName() + " " + employee.getLastName());

                    employees.add(employee);
                } catch (Exception e) {
                    throw new ImportException("Erreur ligne " + lineNumber + ": " + e.getMessage());
                }
            }
        }
        return employees;
    }

    private Map<String, SalaryStructure> parseStructures(MultipartFile file) throws Exception {
        Map<String, SalaryStructure> structures = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line = reader.readLine(); // Skip header
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (structures.size() >= MAX_RECORDS) {
                    throw new ImportException("Nombre maximum de structures dépassé");
                }

                String[] columns = line.split(",");
                validateStructureColumns(columns, lineNumber);

                String structureName = columns[0].trim();
                SalaryStructure structure = structures.computeIfAbsent(structureName, k -> {
                    SalaryStructure s = new SalaryStructure();
                    s.setName(k);
                    s.setCompany(columns[5].trim());
                    s.setSalaryComponents(new ArrayList<>());
                    return s;
                });

                SalaryComponent component = new SalaryComponent();
                component.setSalaryComponent(columns[1].trim());
                component.setSalaryComponentAbbr(columns[2].trim());
                component.setType(columns[3].trim());
                component.setFormula(columns[4].trim());
                structure.getSalaryComponents().add(component);
            }
        }
        return structures;
    }

    @SuppressWarnings("unchecked")
    private List<SalaryStructureAssignment> parseAssignments(
            MultipartFile file, 
            Set<Integer> validEmployeeRefs,
            Set<String> validStructures) throws Exception {
        
        List<SalaryStructureAssignment> assignments = new ArrayList<>();
        Map<Integer, EmployeeDto> employeeMap = (Map<Integer, EmployeeDto>) importCache.get("employee_map");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line = reader.readLine(); // Skip header
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (assignments.size() >= MAX_RECORDS) {
                    throw new ImportException("Nombre maximum d'assignations dépassé");
                }

                String[] columns = line.split(",");
                validateAssignmentColumns(columns, lineNumber);

                try {
                    SalaryStructureAssignment assignment = new SalaryStructureAssignment();
                    assignment.setFrom_date(ValueController.validateDate(columns[0].trim()));
                    
                    int employeeRef = Integer.parseInt(columns[1].trim());
                    if (!validEmployeeRefs.contains(employeeRef)) {
                        throw new ImportException("Référence employé invalide: " + employeeRef);
                    }
                    assignment.setEmployee_ref(employeeRef);
                    
                    assignment.setBase(Double.parseDouble(columns[2].trim()));
                    
                    String structureName = columns[3].trim();
                    if (!validStructures.contains(structureName)) {
                        throw new ImportException("Structure salariale invalide: " + structureName);
                    }
                    assignment.setSalary_structure(structureName);
                    
                    assignment.setCompany("Admin");
                    assignments.add(assignment);
                } catch (Exception e) {
                    throw new ImportException("Erreur ligne " + lineNumber + ": " + e.getMessage());
                }
            }
        }
        return assignments;
    }

    private String validateSession(HttpSession session) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            throw new ImportException("Session invalide");
        }
        return sid;
    }

    private void validateEmployeeColumns(String[] columns, int lineNumber) {
        if (columns.length < 7) {
            throw new ImportException("Format invalide ligne " + lineNumber + 
                ": attendu 7 colonnes, trouvé " + columns.length);
        }
    }

    private void validateStructureColumns(String[] columns, int lineNumber) {
        if (columns.length < 6) {
            throw new ImportException("Format invalide ligne " + lineNumber + 
                ": attendu 6 colonnes, trouvé " + columns.length);
        }
    }

    private void validateAssignmentColumns(String[] columns, int lineNumber) {
        if (columns.length < 4) {
            throw new ImportException("Format invalide ligne " + lineNumber + 
                ": attendu 4 colonnes, trouvé " + columns.length);
        }
    }

    private void clearImportCache() {
        importCache.clear();
    }
}
