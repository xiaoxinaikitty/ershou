package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dao.product.ProductAddDao;
import com.xuchao.ershou.model.entity.Product;

/**
 * 商品服务接口
 */
public interface ProductService {
    
    /**
     * 添加商品
     * @param userId 当前登录用户ID
     * @param productAddDao 商品信息
     * @return 添加的商品信息
     */
    Product addProduct(Long userId, ProductAddDao productAddDao);
    
    /**
     * 根据商品ID查询商品详情
     * @param productId 商品ID
     * @return 商品详细信息
     */
    Product getProductById(Long productId);
}