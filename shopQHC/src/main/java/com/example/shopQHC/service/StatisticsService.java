package com.example.shopQHC.service;

import java.math.BigDecimal;
import java.util.List;

public interface StatisticsService {

    // Khách hàng
    long getTotalCustomers();
    List<Object[]> getTopCustomersByOrderCount();
    List<Object[]> getTopCustomersBySpending();

    // Sản phẩm
    List<Object[]> getTopSellingProducts();

    // Doanh thu
    BigDecimal getTotalRevenue();
    List<Object[]> getMonthlyRevenue(int year);
}