package com.xuchao.ershou.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 营销活动图片视图对象
 */
@Data
public class PromotionImageVO {
    
    /**
     * 图片ID
     */
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
     * 图片类型代码
     */
    private Integer imageType;
    
    /**
     * 图片类型描述
     */
    private String imageTypeDesc;
    
    /**
     * 排序号
     */
    private Integer sortOrder;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
} 