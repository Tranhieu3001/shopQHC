package com.example.shopQHC.controller;

import com.example.shopQHC.dto.response.CouponApplyResponse;
import com.example.shopQHC.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/coupon/apply")
    public CouponApplyResponse applyCoupon(@RequestParam String code,
                                           @RequestParam BigDecimal totalAmount) {
        return couponService.applyCoupon(code, totalAmount);
    }
}