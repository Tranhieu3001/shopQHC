package com.example.shopQHC.controller;

import com.example.shopQHC.entity.Order;
import com.example.shopQHC.entity.User;
import com.example.shopQHC.repository.UserRepository;
import com.example.shopQHC.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class UserOrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
    }

    @GetMapping
    public String orderHistory(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        model.addAttribute("orders", orderService.getOrdersByUserId(user.getId()));
        return "user/orders";
    }
    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        orderService.cancelOrder(id, username);
        return "redirect:/orders";
    }
    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id,
                              Authentication authentication,
                              Model model) {

        User user = getCurrentUser(authentication);
        Order order = orderService.getOrderById(id);

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Không có quyền");
        }

        model.addAttribute("order", order);
        return "user/order-detail";
    }
}