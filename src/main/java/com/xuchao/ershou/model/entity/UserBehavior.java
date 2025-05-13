package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户行为实体类
 */
@Data
@TableName("user_behavior")
public class UserBehavior {
    
    /**
     * 行为记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 行为类型(1浏览 2收藏 3加购 4购买 5评价)
     */
    private Integer behaviorType;
    
    /**
     * 行为发生时间
     */
    private LocalDateTime behaviorTime;
    
    /**
     * 停留时间(秒)
     */
    private Integer stayTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
} 