package com.example.demo.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.models.SupplierQuotation;
import com.example.demo.models.SupplierQuotationItem;
import com.example.demo.services.ItemService;
import com.example.demo.services.SupplierQuotationService;
import com.example.demo.services.WarehouseService;
import com.example.demo.utils.ValueController;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/supplier-quotation")
public class SupplierQuotationController {

    @Autowired
    private SupplierQuotationService supplierQuotationService;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private ItemService itemService;
    
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

    @GetMapping("/create/{supplier}")
    public String getSupplierQuotationForm(@PathVariable String supplier, HttpSession session, Model model, RedirectAttributes redirectAttributes){
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }

        SupplierQuotation supplierQuotation = new SupplierQuotation();
        supplierQuotation.setSupplier(supplier);
        supplierQuotation.setTransactionDate(LocalDate.now().toString());
        supplierQuotation.setItems(new ArrayList<>());

        try {
            model.addAttribute("items", itemService.getItems(sid));
            model.addAttribute("warehouses", warehouseService.getWarehouses(sid));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Could not fetch items & warehouses: "+e.getMessage());
            return "redirect:/supplier/list";
        }

        model.addAttribute("supplierQuotation", supplierQuotation);
        return "/supplier/supplier-quotation-form";
    }

    @PostMapping("/save")
    public String createSupplierQuotation(HttpSession session, SupplierQuotation supplierQuotation, Map<String, Object> ){
        System.out.println("Creating sq");
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }

        return "redirect:/supplier/list";
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

    @GetMapping("/validate/{id}")
    public String validateSupplierQuotation(@PathVariable String id, HttpSession session, RedirectAttributes redirectAttributes) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }

        try {
            supplierQuotationService.validateSupplierQuotation(id, sid);
            redirectAttributes.addFlashAttribute("success", "Supplier Quotation validated successfully.");
        } catch (HttpClientErrorException e) {
            redirectAttributes.addFlashAttribute("error", "Unable to validate Supplier Quotation: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Unable to validate Supplier Quotation: " + e.getMessage());
        }

        return "redirect:/supplier-quotation/" + id;
    }

    @PostMapping("item/update/{itemId}")
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
            supplierQuotationService.updateSupplierQuotationItemRate(itemId, sid, rate);
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
