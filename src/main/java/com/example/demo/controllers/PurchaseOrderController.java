package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.models.PurchaseInvoice;
import com.example.demo.models.PurchaseOrder;
import com.example.demo.services.PurchaseInvoiceService;
import com.example.demo.services.PurchaseOrderService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/purchase-order")
public class PurchaseOrderController {
    
    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private PurchaseInvoiceService purchaseInvoiceService;

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

    @GetMapping("/{id}")
    public String getPurchaseInvoicesByPurchaseOrder(@PathVariable String id, HttpSession session, Model model) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null) {
            return "redirect:/auth/login";
        }

        try {
            PurchaseOrder purchaseOrder = purchaseOrderService.getPurchaseOrder(sid, id);
            model.addAttribute("order", purchaseOrder);
            List<PurchaseInvoice> purchaseInvoices = purchaseInvoiceService.getPurchaseInvoicesByPurchaseOrder(id, sid);
            model.addAttribute("invoices", purchaseInvoices);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Unable to fetch invoices : " + e.getMessage());
        }
        return "supplier/purchase-orders-details";
    }
}
