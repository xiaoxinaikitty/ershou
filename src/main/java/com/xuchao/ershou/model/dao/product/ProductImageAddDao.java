package com.xuchao.ershou.model.dao.product;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 添加商品图片的数据传输对象
 */
@Data
public class ProductImageAddDao {
    
    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    /**
     * 是否为主图(0否 1是)
     */
    private Integer isMain = 0;
    
    /**
     * 图片排序
     */
    private Integer sortOrder = 0;
}
