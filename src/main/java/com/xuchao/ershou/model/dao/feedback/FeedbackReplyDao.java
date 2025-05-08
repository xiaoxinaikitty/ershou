package com.xuchao.ershou.model.dao.feedback;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 回复用户反馈的请求DAO
 */
@Data
public class FeedbackReplyDao {

    /**
     * 反馈ID
     */
    @NotNull(message = "反馈ID不能为空")
    private Long feedbackId;

    /**
     * 回复内容
     */
    @NotBlank(message = "回复内容不能为空")
    private String adminReply;

    /**
     * 处理状态(0未处理 1处理中 2已处理)
     */
    @NotNull(message = "处理状态不能为空")
    private Integer status;
} 