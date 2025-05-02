package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.services.SupplierQuotationItemService;
import com.example.demo.utils.ValueController;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/supplier-quotation-item")
public class SupplierQuotationItemController {

    @Autowired
    private SupplierQuotationItemService supplierQuotationItemService;
    
    @PostMapping("/update/{itemId}")
    public String updateSupplierQuotationItem(@PathVariable String itemId, 
                                              @RequestParam String parentId, 
                                              @RequestParam String newRate, 
                                              HttpSession session, 
                                              Model model,
                                              RedirectAttributes redirectAttributes) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }

        try {
            double rate = ValueController.parseValue(newRate);
            supplierQuotationItemService.updateSupplierQuotationItemRate(itemId, sid, rate);
        } catch (HttpClientErrorException e) {
            redirectAttributes.addFlashAttribute("error", "Unable to update Supplier Quotation Item: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Unable to update Supplier Quotation Item: " + e.getMessage());
        }

        redirectAttributes.addFlashAttribute("success", "Supplier Quotation Item updated successfully.");
        return "redirect:/supplier-quotation/" + parentId;
    }
}
