package com.xuchao.ershou.model.dao.promotion;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 添加营销活动图片的请求DAO
 */
@Data
public class PromotionImageAddDao {

    /**
     * 图片URL
     */
    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;

    /**
     * 图片类型(1轮播图 2展示图)
     */
    @NotNull(message = "图片类型不能为空")
    private Integer imageType;

    /**
     * 排序号，值越大越靠前
     */
    private Integer sortOrder;
} 