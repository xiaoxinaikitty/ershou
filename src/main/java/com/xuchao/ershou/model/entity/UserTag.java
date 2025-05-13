package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户标签实体类
 */
@Data
@TableName("user_tag")
public class UserTag {
    
    /**
     * 标签ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 标签名称
     */
    private String tagName;
    
    /**
     * 标签权重值
     */
    private BigDecimal tagValue;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
} 