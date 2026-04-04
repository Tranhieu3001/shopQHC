package com.example.shopQHC.service;

import com.example.shopQHC.entity.Order;
import com.example.shopQHC.entity.Order.OrderStatus;

import java.util.List;

public interface OrderService {

    List<Order> getAllOrders(String keyword, OrderStatus orderStatus);

    Order getOrderById(Long id);

    Order updateOrderStatus(Long id, OrderStatus orderStatus);
    void cancelOrder(Long orderId, String username);
    List<Order> getOrdersByUserId(Long userId);
}