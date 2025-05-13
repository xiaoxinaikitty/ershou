package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 相似商品实体类
 */
@Data
@TableName("similar_products")
public class SimilarProducts {
    
    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 相似商品ID
     */
    private Long similarProductId;
    
    /**
     * 相似度得分
     */
    private BigDecimal similarityScore;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
} 