package com.xuchao.ershou.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户反馈的视图对象
 */
@Data
public class UserFeedbackVO {

    /**
     * 反馈ID
     */
    private Long feedbackId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 反馈类型代码
     */
    private Integer feedbackType;

    /**
     * 反馈类型描述
     */
    private String feedbackTypeDesc;

    /**
     * 反馈标题
     */
    private String feedbackTitle;

    /**
     * 反馈内容详情
     */
    private String feedbackContent;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 反馈附带的图片URL
     */
    private String images;

    /**
     * 处理状态代码
     */
    private Integer status;

    /**
     * 处理状态描述
     */
    private String statusDesc;

    /**
     * 优先级代码
     */
    private Integer priorityLevel;

    /**
     * 优先级描述
     */
    private String priorityLevelDesc;

    /**
     * 管理员ID
     */
    private Long adminId;

    /**
     * 管理员回复内容
     */
    private String adminReply;

    /**
     * 回复时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime replyTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
} 