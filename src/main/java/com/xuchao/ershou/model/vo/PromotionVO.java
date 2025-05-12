package com.xuchao.ershou.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 营销活动视图对象
 */
@Data
public class PromotionVO {
    
    /**
     * 营销活动ID
     */
    private Long promotionId;
    
    /**
     * 活动标题
     */
    private String title;
    
    /**
     * 活动描述
     */
    private String description;
    
    /**
     * 活动类型代码
     */
    private Integer promotionType;
    
    /**
     * 活动类型描述
     */
    private String promotionTypeDesc;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 状态代码
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 排序号
     */
    private Integer sortOrder;
    
    /**
     * 点击跳转链接
     */
    private String urlLink;
    
    /**
     * 创建人ID
     */
    private Long createdBy;
    
    /**
     * 创建人用户名
     */
    private String createdByUsername;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
    
    /**
     * 活动图片列表
     */
    private List<PromotionImageVO> images;
} 