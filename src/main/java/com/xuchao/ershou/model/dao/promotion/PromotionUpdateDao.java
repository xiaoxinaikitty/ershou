package com.xuchao.ershou.model.dao.promotion;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 更新营销活动的请求DAO
 */
@Data
public class PromotionUpdateDao {

    /**
     * 营销活动ID
     */
    @NotNull(message = "活动ID不能为空")
    private Long promotionId;

    /**
     * 活动标题
     */
    @NotBlank(message = "活动标题不能为空")
    @Size(max = 100, message = "活动标题最多100个字符")
    private String title;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 活动类型(1促销活动 2折扣 3满减 4优惠券)
     */
    @NotNull(message = "活动类型不能为空")
    private Integer promotionType;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 状态(0下线 1上线)
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 排序号，值越大越靠前
     */
    private Integer sortOrder;

    /**
     * 点击跳转链接
     */
    private String urlLink;

    /**
     * 图片URL列表
     */
    private List<PromotionImageAddDao> images;
} 