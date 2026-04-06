package com.example.shopQHC.controller.admin;

import com.example.shopQHC.entity.Order;
import com.example.shopQHC.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public String getOrderList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Order.OrderStatus orderStatus,
            Model model
    ) {
        model.addAttribute("orders", orderService.getAllOrders(keyword, orderStatus));
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", orderStatus);
        model.addAttribute("statuses", Order.OrderStatus.values());
        return "admin/orders";
    }

    @GetMapping("/{id}")
    public String getOrderDetail(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getOrderById(id));
        model.addAttribute("statuses", Order.OrderStatus.values());
        return "admin/order-detail-2";
    }

    @PostMapping("/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus orderStatus
    ) {
        orderService.updateOrderStatus(id, orderStatus);
        return "redirect:/admin/orders/" + id;
    }
}