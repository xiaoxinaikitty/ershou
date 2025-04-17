package com.xuchao.ershou.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品列表展示对象
 */
@Data
public class ProductPageVO {
    
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
     * 物品原价
     */
    private BigDecimal originalPrice;
    
    /**
     * 商品分类ID
     */
    private Integer categoryId;
    
    /**
     * 商品分类名称
     */
    private String categoryName;
    
    /**
     * 发布用户ID
     */
    private Long userId;
    
    /**
     * 发布用户名
     */
    private String username;
    
    /**
     * 物品成色(1-10级)
     */
    private Integer conditionLevel;
    
    /**
     * 商品所在地
     */
    private String location;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 发布时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 主图URL
     */
    private String mainImageUrl;
} 