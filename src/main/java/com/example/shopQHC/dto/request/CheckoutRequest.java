package com.example.shopQHC.dto;

import com.example.shopQHC.entity.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequest {
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
    private String note;
    private String couponCode;
    private Order.PaymentMethod paymentMethod;
}