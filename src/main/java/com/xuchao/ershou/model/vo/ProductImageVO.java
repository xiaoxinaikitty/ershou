package com.xuchao.ershou.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商品图片视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageVO {
    /**
     * 图片ID
     */
    private Long imageId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 是否为主图(0否 1是)
     */
    private Integer isMain;
    
    /**
     * 图片排序
     */
    private Integer sortOrder;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}
