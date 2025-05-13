package com.xuchao.ershou.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 用户行为DTO对象
 */
@Data
public class UserBehaviorDTO {
    
    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    /**
     * 行为类型(1浏览 2收藏 3加购 4购买 5评价)
     */
    @NotNull(message = "行为类型不能为空")
    private Integer behaviorType;
    
    /**
     * 停留时间(秒)，浏览行为时使用
     */
    private Integer stayTime;
} 