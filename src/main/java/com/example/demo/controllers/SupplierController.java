package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.services.SupplierService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping("/list")
    public String listSuppliers(HttpSession session, Model model) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }

        try {
            model.addAttribute("suppliers", supplierService.fetchSuppliers(sid));
            return "supplier/suppliers";
        } catch (Exception e) {
            model.addAttribute("error", "Impossible de récupérer les fournisseurs : " + e.getMessage());
            return "supplier/suppliers";
        }
    }
}
