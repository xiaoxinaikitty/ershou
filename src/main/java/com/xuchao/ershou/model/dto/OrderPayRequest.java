package com.xuchao.ershou.model.dto;

import lombok.Data;

@Data
public class OrderPayRequest {
    private Long orderId;
    private Long userId;
    private Integer paymentType;
    private Integer paymentChannel;
    private String transactionNo;
} 