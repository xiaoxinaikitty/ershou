package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户会话表实体类
 */
@Data
@TableName("conversation")
@Accessors(chain = true)
public class Conversation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    @TableId(value = "conversation_id", type = IdType.AUTO)
    private Long conversationId;

    /**
     * 关联商品ID
     */
    private Long productId;

    /**
     * 普通用户ID
     */
    private Long userId;

    /**
     * 卖家用户ID
     */
    private Long sellerId;

    /**
     * 最后消息内容预览
     */
    private String lastMessageContent;

    /**
     * 最后消息时间
     */
    private LocalDateTime lastMessageTime;

    /**
     * 用户未读消息数
     */
    private Integer userUnreadCount;

    /**
     * 卖家未读消息数
     */
    private Integer sellerUnreadCount;

    /**
     * 会话状态(0已关闭 1活跃)
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
} 