package com.example.shopQHC.service.impl;

import com.example.shopQHC.entity.Order;
import com.example.shopQHC.entity.Order.OrderStatus;
import com.example.shopQHC.repository.OrderRepository;
import com.example.shopQHC.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service


@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<Order> getAllOrders(String keyword, OrderStatus orderStatus) {
        if (!StringUtils.hasText(keyword)) {
            keyword = null;
        }
        return orderRepository.searchOrders(keyword, orderStatus);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findWithDetailsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng với id = " + id));
    }

    @Override
    public Order updateOrderStatus(Long id, OrderStatus orderStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng với id = " + id));

        order.setOrderStatus(orderStatus);
        return orderRepository.save(order);
    }
    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    @Override
    public void cancelOrder(Long orderId, String username) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // check đúng user
        if (!order.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Không có quyền");
        }

        // không cho hủy nếu đang giao hoặc đã xong
        if (order.getOrderStatus() == Order.OrderStatus.SHIPPING ||
                order.getOrderStatus() == Order.OrderStatus.COMPLETED ||
                order.getOrderStatus() == Order.OrderStatus.CANCELLED) {
            throw new RuntimeException("Không thể hủy đơn");
        }

        order.setOrderStatus(Order.OrderStatus.CANCELLED);

        orderRepository.save(order);
    }
}