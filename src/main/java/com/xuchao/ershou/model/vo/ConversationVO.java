package com.xuchao.ershou.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会话视图对象
 */
@Data
public class ConversationVO {

    /**
     * 会话ID
     */
    private Long conversationId;

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
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 卖家ID
     */
    private Long sellerId;

    /**
     * 卖家用户名
     */
    private String sellerUsername;

    /**
     * 卖家头像
     */
    private String sellerAvatar;

    /**
     * 最后消息内容预览
     */
    private String lastMessageContent;

    /**
     * 最后消息时间
     */
    private LocalDateTime lastMessageTime;

    /**
     * 未读消息数
     */
    private Integer unreadCount;

    /**
     * 会话状态(0已关闭 1活跃)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
} 