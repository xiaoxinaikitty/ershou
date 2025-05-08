package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户反馈表实体类
 */
@Data
@TableName("user_feedback")
@Accessors(chain = true)
public class UserFeedback implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "feedback_id", type = IdType.AUTO)
    private Long feedbackId;

    @TableField("user_id")
    private Long userId;

    @TableField("feedback_type")
    private Integer feedbackType;

    @TableField("feedback_title")
    private String feedbackTitle;

    @TableField("feedback_content")
    private String feedbackContent;

    @TableField("contact_info")
    private String contactInfo;

    @TableField("images")
    private String images;

    @TableField("status")
    private Integer status;

    @TableField("priority_level")
    private Integer priorityLevel;

    @TableField("admin_id")
    private Long adminId;

    @TableField("admin_reply")
    private String adminReply;

    @TableField("reply_time")
    private LocalDateTime replyTime;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
} 