package com.xuchao.ershou.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单消息实体类
 */
@Data
public class OrderMessage {
    /**
     * 消息ID
     */
    private Long messageId;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 接收者ID
     */
    private Long receiverId;
    
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