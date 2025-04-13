package com.xuchao.ershou.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商品收藏视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFavoriteVO {
    /**
     * 收藏ID
     */
    private Long favoriteId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品标题
     */
    private String productTitle;
    
    /**
     * 商品价格
     */
    private String productPrice;
    
    /**
     * 商品主图
     */
    private String productImage;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}
