package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 营销活动实体类
 */
@Data
@TableName("promotion")
public class Promotion {
    
    /**
     * 营销活动ID
     */
    @TableId(value = "promotion_id", type = IdType.AUTO)
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
     * 活动类型(1促销活动 2折扣 3满减 4优惠券)
     */
    private Integer promotionType;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 状态(0下线 1上线)
     */
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
     * 创建人ID
     */
    private Long createdBy;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
} 