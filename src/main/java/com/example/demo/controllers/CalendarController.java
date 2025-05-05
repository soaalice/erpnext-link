package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/calendar")
public class CalendarController {
    
    @GetMapping
    public String getCalendar(HttpSession session){
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }

        return "calendar";
    }
}
