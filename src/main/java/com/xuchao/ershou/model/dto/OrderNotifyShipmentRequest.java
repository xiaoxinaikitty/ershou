package com.xuchao.ershou.model.dto;

import lombok.Data;

/**
 * 通知收货请求DTO
 */
@Data
public class OrderNotifyShipmentRequest {
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 卖家用户ID
     */
    private Long sellerId;
} 