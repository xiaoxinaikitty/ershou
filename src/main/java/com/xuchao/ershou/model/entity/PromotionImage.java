package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 营销活动图片实体类
 */
@Data
@TableName("promotion_image")
public class PromotionImage {
    
    /**
     * 图片ID
     */
    @TableId(value = "image_id", type = IdType.AUTO)
    private Long imageId;
    
    /**
     * 关联的营销活动ID
     */
    private Long promotionId;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 图片类型(1轮播图 2展示图)
     */
    private Integer imageType;
    
    /**
     * 排序号，值越大越靠前
     */
    private Integer sortOrder;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
} 