package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.models.PurchaseOrder;
import com.example.demo.services.PurchaseOrderService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/purchase-order")
public class PurchaseOrderController {
    
    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping("/supplier/{id}")
    public String getPurchaseOrderBySupplierById(@PathVariable String id, HttpSession session, Model model) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }

        try {
            List<PurchaseOrder> purchaseOrders = purchaseOrderService.getPurchaseOrdersBySupplier(id, sid);
            model.addAttribute("orders", purchaseOrders);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Unable to fetch requests : " + e.getMessage());
        }

        return "supplier/purchase-orders";
    }
}
