package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 推荐记录实体类
 */
@Data
@TableName("recommendation_record")
public class RecommendationRecord {
    
    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 推荐商品ID
     */
    private Long productId;
    
    /**
     * 推荐类型(1相似商品 2猜你喜欢 3热门推荐 4新品推荐)
     */
    private Integer recommendationType;
    
    /**
     * 推荐得分
     */
    private BigDecimal score;
    
    /**
     * 是否被点击(0否 1是)
     */
    private Boolean isClicked;
    
    /**
     * 是否转化(0否 1是)
     */
    private Boolean isConverted;
    
    /**
     * 推荐时间
     */
    private LocalDateTime recommendTime;
    
    /**
     * 点击时间
     */
    private LocalDateTime clickTime;
} 