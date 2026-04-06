package com.example.shopQHC.controller;

import com.example.shopQHC.entity.Order;
import com.example.shopQHC.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final OrderRepository orderRepository;

    @GetMapping("/qr/{orderId}")
    public String showQrPage(@PathVariable Long orderId, Model model) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng id = " + orderId));

        if (order.getOrderStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("Đơn hàng đã bị hủy, không thể thanh toán");
        }

        String bank = "TPB";
        String accountNo = "59834566789";
        String accountName = "TRAN HIEU";

        String orderCode = "DH" + order.getId();

        BigDecimal finalAmount = order.getFinalAmount() != null ? order.getFinalAmount() : BigDecimal.ZERO;
        String amount = finalAmount.setScale(0, java.math.RoundingMode.HALF_UP).toPlainString();

        String qrUrl = String.format(
                "https://img.vietqr.io/image/%s-%s-compact2.png?amount=%s&addInfo=%s&accountName=%s",
                bank,
                accountNo,
                amount,
                orderCode,
                accountName.replace(" ", "%20")
        );

        model.addAttribute("order", order);
        model.addAttribute("orderCode", orderCode);
        model.addAttribute("qrUrl", qrUrl);

        model.addAttribute("bank", bank);
        model.addAttribute("accountNo", accountNo);
        model.addAttribute("accountName", accountName);

        return "payment/qr-payment";
    }

    @GetMapping("/check-status/{orderId}")
    @ResponseBody
    public String checkStatus(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order != null &&
                (order.getOrderStatus() == Order.OrderStatus.CONFIRMED
                        || order.getOrderStatus() == Order.OrderStatus.COMPLETED)) {
            return "PAID";
        }

        return "PENDING";
    }
}