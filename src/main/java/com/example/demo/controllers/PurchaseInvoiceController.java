package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.models.PurchaseInvoice;
import com.example.demo.services.PurchaseInvoiceService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/purchase-invoice")
public class PurchaseInvoiceController {
    
    @Autowired
    private PurchaseInvoiceService purchaseInvoiceService;
    
    @GetMapping("/list")
    public String getPurchaseInvoiceBySupplierById(HttpSession session, Model model) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }

        try {
            List<PurchaseInvoice> purchaseInvoices = purchaseInvoiceService.getPurchaseInvoices(sid);
            model.addAttribute("invoices", purchaseInvoices);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Unable to fetch invoices : " + e.getMessage());
        }
        return "accounting/purchase-invoices";
    }
}
