package com.example.shopQHC.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentWebhookRequest {
    private Double transferAmount;
    private String content;
}