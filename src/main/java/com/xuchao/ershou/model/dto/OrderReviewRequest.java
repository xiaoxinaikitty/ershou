package com.xuchao.ershou.model.dto;

import lombok.Data;

/**
 * 订单评价请求DTO
 */
@Data
public class OrderReviewRequest {
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 评价用户ID（买家）
     */
    private Long userId;
    
    /**
     * 卖家用户ID
     */
    private Long sellerId;
    
    /**
     * 评价内容
     */
    private String content;
    
    /**
     * 评分（1-5）
     */
    private Integer rating;
    
    /**
     * 是否匿名（0否 1是）
     */
    private Integer isAnonymous = 0;
} 