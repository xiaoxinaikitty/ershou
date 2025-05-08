package com.xuchao.ershou.model.dao.feedback;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 添加用户反馈的请求DAO
 */
@Data
public class FeedbackAddDao {

    /**
     * 反馈类型(1功能建议 2体验问题 3商品相关 4物流相关 5其他)
     */
    @NotNull(message = "反馈类型不能为空")
    private Integer feedbackType;

    /**
     * 反馈标题
     */
    @NotBlank(message = "反馈标题不能为空")
    @Size(max = 100, message = "反馈标题最多100个字符")
    private String feedbackTitle;

    /**
     * 反馈内容详情
     */
    @NotBlank(message = "反馈内容不能为空")
    private String feedbackContent;

    /**
     * 联系方式（电话或邮箱，可选）
     */
    @Size(max = 100, message = "联系方式最多100个字符")
    private String contactInfo;

    /**
     * 反馈附带的图片URL，多个URL使用逗号分隔
     */
    private String images;
} 