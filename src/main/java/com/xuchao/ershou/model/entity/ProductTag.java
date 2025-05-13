package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品标签实体类
 */
@Data
@TableName("product_tag")
public class ProductTag {
    
    /**
     * 标签ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 标签名称
     */
    private String tagName;
    
    /**
     * 标签权重值
     */
    private BigDecimal tagValue;
    
    /**
     * 是否自动生成(0手动 1自动)
     */
    private Boolean isAuto;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
} 