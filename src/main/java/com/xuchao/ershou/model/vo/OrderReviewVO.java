package com.xuchao.ershou.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单评价视图对象
 */
@Data
public class OrderReviewVO {
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
     * 商品标题
     */
    private String productTitle;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名（匿名则不显示）
     */
    private String username;
    
    /**
     * 卖家ID
     */
    private Long sellerId;
    
    /**
     * 卖家用户名
     */
    private String sellerName;
    
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