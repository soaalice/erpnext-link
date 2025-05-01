package com.example.demo.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute
    public void addGlobalAttributes(HttpSession session, Model model) {
        boolean loggedIn = session != null && session.getAttribute("sid") != null;
        model.addAttribute("loggedIn", loggedIn);
    }
}
