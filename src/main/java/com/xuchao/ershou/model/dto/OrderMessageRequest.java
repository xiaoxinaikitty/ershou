package com.xuchao.ershou.model.dto;

import lombok.Data;

/**
 * 订单消息请求DTO
 */
@Data
public class OrderMessageRequest {
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 发送者ID（通常是买家）
     */
    private Long senderId;
    
    /**
     * 接收者ID（通常是卖家）
     */
    private Long receiverId;
    
    /**
     * 消息内容
     */
    private String content;
} 