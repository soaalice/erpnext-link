package com.example.demo.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.example.demo.config.ErpApiConfig;

import org.springframework.web.client.HttpClientErrorException;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    @GetMapping("/login")
    public String login() {
        return "account/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        String sid = (String) session.getAttribute("sid");

        if (sid != null) {
            String erpLogoutUrl = ErpApiConfig.ERP_URL_METHOD + "/logout";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", sid);

            HttpEntity<String> request = new HttpEntity<>(headers);

            try {
                restTemplate.exchange(erpLogoutUrl, HttpMethod.POST, request, String.class);
                System.out.println("Déconnecté de ERPNext avec succès");
            } catch (Exception e) {
                System.err.println("Erreur de déconnexion ERPNext: " + e.getMessage());
            }
        }
        
        session.invalidate();
        return "redirect:/auth/login";
    }

    
    @PostMapping("/login")
    public String loginPost(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {

        String erpUrl = ErpApiConfig.ERP_URL_METHOD + "/login";
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> creds = new HashMap<>();
        creds.put("usr", username);
        creds.put("pwd", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, String>> request = new HttpEntity<>(creds, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(erpUrl, request, String.class);

            List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (cookies != null) {
                for (String cookie : cookies) {
                    if (cookie.startsWith("sid=")) {
                        String sid = cookie.split(";")[0];
                        session.setAttribute("sid", sid);
                        System.out.println("ERP Session ID: " + sid);
                    }
                }
            }

            return "redirect:/";

        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Identifiants invalides");
            return "account/login";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur de connexion à ERPNext : " + e.getMessage());
            return "account/login";
        }
    }
}
