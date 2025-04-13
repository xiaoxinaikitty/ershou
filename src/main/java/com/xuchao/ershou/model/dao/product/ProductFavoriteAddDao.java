package com.xuchao.ershou.model.dao.product;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 收藏商品的数据传输对象
 */
@Data
public class ProductFavoriteAddDao {
    
    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;
}
