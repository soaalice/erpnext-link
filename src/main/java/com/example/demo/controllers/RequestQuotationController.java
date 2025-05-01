package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.models.RequestQuotation;
import com.example.demo.services.RequestQuotationService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/request-quotation")
public class RequestQuotationController {

    @Autowired
    private RequestQuotationService requestQuotationService;
    
    @GetMapping("/supplier/{id}")
    public String getRequestQuotationBySupplierById(@PathVariable String id, HttpSession session, Model model) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }

        try {
            List<RequestQuotation> requestQuotations = requestQuotationService.getRequestQuotationsBySupplier(id, sid);
            model.addAttribute("quotations", requestQuotations);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Unable to fetch requests : " + e.getMessage());
        }

        return "supplier/request-quotations";
    }
    
}
