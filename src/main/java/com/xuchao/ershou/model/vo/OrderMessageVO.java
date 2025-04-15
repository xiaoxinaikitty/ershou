package com.xuchao.ershou.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单消息视图对象
 */
@Data
public class OrderMessageVO {
    /**
     * 消息ID
     */
    private Long messageId;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 订单编号
     */
    private String orderNo;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品标题
     */
    private String productTitle;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 发送者名称
     */
    private String senderName;
    
    /**
     * 接收者ID
     */
    private Long receiverId;
    
    /**
     * 接收者名称
     */
    private String receiverName;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 是否已读（0未读 1已读）
     */
    private Integer isRead;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
} 