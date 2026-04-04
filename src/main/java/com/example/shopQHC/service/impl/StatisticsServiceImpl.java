package com.example.shopQHC.service.impl;

import com.example.shopQHC.entity.Order;
import com.example.shopQHC.entity.User;
import com.example.shopQHC.repository.OrderItemRepository;
import com.example.shopQHC.repository.OrderRepository;
import com.example.shopQHC.repository.UserRepository;
import com.example.shopQHC.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    // ================== KHÁCH HÀNG ==================

    @Override
    public long getTotalCustomers() {
        return userRepository.countByRole(User.Role.CUSTOMER);
    }

    @Override
    public List<Object[]> getTopCustomersByOrderCount() {
        return userRepository.getTopCustomersByOrderCount();
    }

    @Override
    public List<Object[]> getTopCustomersBySpending() {
        return userRepository.getTopCustomersBySpending();
    }

    // ================== SẢN PHẨM ==================

    @Override
    public List<Object[]> getTopSellingProducts() {
        return orderItemRepository.getTop5BestSellingProducts();
    }

    // ================== DOANH THU ==================

    @Override
    public BigDecimal getTotalRevenue() {
        return orderRepository.sumRevenueByStatuses(List.of(
                Order.OrderStatus.CONFIRMED,
                Order.OrderStatus.SHIPPING,
                Order.OrderStatus.COMPLETED
        ));
    }

    @Override
    public List<Object[]> getMonthlyRevenue(int year) {
        return orderRepository.getMonthlyRevenue(year);
    }
}