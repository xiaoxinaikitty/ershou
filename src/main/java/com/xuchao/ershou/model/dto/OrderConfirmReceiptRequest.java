package com.xuchao.ershou.model.dto;

import lombok.Data;

/**
 * 确认收货请求DTO
 */
@Data
public class OrderConfirmReceiptRequest {
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 用户ID（买家）
     */
    private Long userId;
} 