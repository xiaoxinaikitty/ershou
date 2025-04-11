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
}