package com.example.shopQHC.controller;

import com.example.shopQHC.dto.request.PaymentWebhookRequest;
import com.example.shopQHC.entity.Order;
import com.example.shopQHC.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class WebhookController {

    private final OrderRepository orderRepository;

    @PostMapping("/sepay-callback")
    public String handleWebhook(@RequestBody PaymentWebhookRequest request) {
        if (request == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            return "INVALID";
        }

        String content = request.getContent().trim().toUpperCase();

        if (!content.contains("DH")) {
            return "IGNORED";
        }

        try {
            String numberPart = content.substring(content.indexOf("DH") + 2).replaceAll("[^0-9]", "");
            Long orderId = Long.parseLong(numberPart);

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng id = " + orderId));

            if (order.getOrderStatus() == Order.OrderStatus.PENDING) {
                order.setOrderStatus(Order.OrderStatus.CONFIRMED);
                orderRepository.save(order);
            }

            return "OK";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}