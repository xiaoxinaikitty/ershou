package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dao.product.ProductImageAddDao;
import com.xuchao.ershou.model.entity.ProductImage;
import com.xuchao.ershou.model.vo.ProductImageVO;

/**
 * 商品图片服务接口
 */
public interface ProductImageService {
    
    /**
     * 添加商品图片
     * @param userId 当前登录用户ID
     * @param productImageAddDao 商品图片信息
     * @return 添加的商品图片信息
     */
    ProductImageVO addProductImage(Long userId, ProductImageAddDao productImageAddDao);
    
    /**
     * 删除商品图片
     * @param userId 当前登录用户ID
     * @param productId 商品ID
     * @param imageId 图片ID
     * @return 是否删除成功
     */
    boolean deleteProductImage(Long userId, Long productId, Long imageId);
}
