package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.models.SupplierQuotation;
import com.example.demo.models.SupplierQuotationItem;
import com.example.demo.services.SupplierQuotationService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/supplier-quotation")
public class SupplierQuotationController {

    @Autowired
    private SupplierQuotationService supplierQuotationService;
    
    @GetMapping("/supplier/{id}")
    public String getSupplierQuotationBySupplierById(@PathVariable String id, HttpSession session, Model model) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }

        try {
            List<SupplierQuotation> supplierQuotations = supplierQuotationService.getSupplierQuotationsBySupplier(id, sid);
            model.addAttribute("quotations", supplierQuotations);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Unable to fetch requests : " + e.getMessage());
        }

        return "supplier/supplier-quotations";
    }

    @GetMapping("/{id}")
    public String getSupplierQuotationById(@PathVariable String id, HttpSession session, Model model) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }

        try {
            SupplierQuotation quotation = supplierQuotationService.fetchSupplierQuotationById(id, sid);

            model.addAttribute("quotation", quotation);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Unable to fetch Supplier Quotation: " + e.getMessage());
        }

        return "supplier/supplier-quotation-details";
    }
    
}
