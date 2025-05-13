package com.xuchao.ershou.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品推荐VO对象
 */
@Data
public class ProductRecommendVO {
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品标题
     */
    private String title;
    
    /**
     * 商品价格
     */
    private BigDecimal price;
    
    /**
     * 商品原价
     */
    private BigDecimal originalPrice;
    
    /**
     * 商品主图URL
     */
    private String mainImage;
    
    /**
     * 推荐得分
     */
    private BigDecimal score;
    
    /**
     * 推荐类型(1相似商品 2猜你喜欢 3热门推荐 4新品推荐)
     */
    private Integer recommendationType;
} 