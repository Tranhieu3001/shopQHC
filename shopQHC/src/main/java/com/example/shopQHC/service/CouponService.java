package com.example.shopQHC.service;

import com.example.shopQHC.dto.response.CouponApplyResponse;
import com.example.shopQHC.entity.Coupon;
import com.example.shopQHC.entity.Coupon.Status;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {

    List<Coupon> getAllCoupons(String keyword, Status status);

    Coupon getCouponById(Long id);

    Coupon saveCoupon(Coupon coupon);

    Coupon updateCoupon(Long id, Coupon coupon);

    void updateCouponStatus(Long id, Status status);

    CouponApplyResponse applyCoupon(String code, BigDecimal totalAmount);
}