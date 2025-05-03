package com.example.demo.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.models.PaymentEntry;
import com.example.demo.services.PaymentEntryService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/payment-entry")
public class PaymentEntryController {

    @Autowired
    private PaymentEntryService paymentEntryService;
    
    @GetMapping("/create/{supplier}/{amount}/{purchaseInvoice}")
    public String createPaymentEntry(@PathVariable String supplier, 
                                     @PathVariable String amount, 
                                     @PathVariable String purchaseInvoice, 
                                     Model model, 
                                     HttpSession session) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }

        PaymentEntry paymentEntry = new PaymentEntry();
        paymentEntry.setParty(supplier);
        paymentEntry.setPartyName(supplier);
        paymentEntry.setTotalAmount(Double.parseDouble(amount));
        model.addAttribute("paymentEntry", paymentEntry);
        model.addAttribute("purchaseInvoice", purchaseInvoice);
        return "accounting/payment-form";
    }

    @PostMapping("/create")
    public String savePaymentEntry(PaymentEntry paymentEntry, 
                                   String purchaseInvoice, 
                                   RedirectAttributes redirectAttributes, 
                                   HttpSession session, Model model) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }

        try {
            paymentEntryService.savePaymentEntry(sid, paymentEntry, purchaseInvoice);
            redirectAttributes.addFlashAttribute("success", "Payment Entry created successfully!");
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            model.addAttribute("error", "Unable to create Payment Entry: " + e.getMessage());
            model.addAttribute("paymentEntry", paymentEntry);
            return "accounting/payment-form";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("paymentEntry", paymentEntry);
            return "accounting/payment-form";
        }
        return "redirect:/purchase-invoice/list";
    }
}
