package com.xuchao.ershou.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单评价实体类
 */
@Data
public class OrderReview {
    /**
     * 评价ID
     */
    private Long reviewId;
    
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
    private Integer isAnonymous;
    
    /**
     * 是否有回复（0否 1是）
     */
    private Integer hasReply;
    
    /**
     * 回复内容
     */
    private String replyContent;
    
    /**
     * 回复时间
     */
    private LocalDateTime replyTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
} 