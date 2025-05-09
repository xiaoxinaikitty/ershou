package com.xuchao.ershou.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户消息视图对象
 */
@Data
public class UserMessageVO {

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品标题
     */
    private String productTitle;

    /**
     * 商品主图
     */
    private String productImage;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 发送者用户名
     */
    private String senderUsername;

    /**
     * 发送者头像
     */
    private String senderAvatar;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 接收者用户名
     */
    private String receiverUsername;

    /**
     * 接收者头像
     */
    private String receiverAvatar;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息附带图片URL
     */
    private String imageUrl;

    /**
     * 是否已读(0未读 1已读)
     */
    private Integer isRead;

    /**
     * 消息发送时间
     */
    private LocalDateTime createdTime;
} 