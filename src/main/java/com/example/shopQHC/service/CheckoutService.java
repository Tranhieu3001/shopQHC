package com.example.shopQHC.service;

import com.example.shopQHC.dto.CheckoutRequest;
import com.example.shopQHC.entity.Order;

public interface CheckoutService {
    Order checkout(Long userId, CheckoutRequest request);

}