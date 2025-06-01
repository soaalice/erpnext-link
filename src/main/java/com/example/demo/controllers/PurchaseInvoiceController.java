package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.models.PurchaseInvoice;
import com.example.demo.services.PdfService;
import com.example.demo.services.PurchaseInvoiceService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/purchase-invoice")
public class PurchaseInvoiceController {
    
    @Autowired
    private PurchaseInvoiceService purchaseInvoiceService;
    
    @GetMapping("/list")
    public String getPurchaseInvoices(HttpSession session, Model model) {
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

    @GetMapping("/calendar/datas")
    public ResponseEntity<?> getPurchaseInvoiceBySupplierById(HttpSession session) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session expired. Please log in again.");
        }

        try {
            List<PurchaseInvoice> purchaseInvoices = purchaseInvoiceService.getPurchaseInvoices(sid);
            return ResponseEntity.ok(purchaseInvoices);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to fetch invoices : " + e.getMessage());
        }
    }

    @GetMapping("/{name}/export")
    public ResponseEntity<?> exportInvoice(HttpSession session, @PathVariable String name) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session expired. Please log in again.");
        }

        try {
            PurchaseInvoice invoice = purchaseInvoiceService.getPurchaseInvoice(sid, name);
            byte[] pdfBytes = PdfService.exportInvoiceToPdf(invoice);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=invoice" + name + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating PDF: " + e.getMessage());
        }
    }
}