package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dao.product.ProductAddDao;
import com.xuchao.ershou.model.dao.product.ProductPageQueryDao;
import com.xuchao.ershou.model.dao.product.ProductUpdateDao;
import com.xuchao.ershou.model.entity.Product;
import com.xuchao.ershou.model.vo.PageResult;
import com.xuchao.ershou.model.vo.ProductPageVO;

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
     * 根据ID查询商品详情
     * @param productId 商品ID
     * @return 商品详细信息
     */
    Product getProductById(Long productId);
    
    /**
     * 更新商品信息
     * @param userId 当前登录用户ID
     * @param productUpdateDao 商品更新信息
     * @return 更新后的商品信息
     */
    Product updateProduct(Long userId, ProductUpdateDao productUpdateDao);
    
    /**
     * 删除商品（将状态设置为下架）
     * @param userId 当前登录用户ID
     * @param productId 商品ID
     * @return 操作是否成功
     */
    boolean deleteProduct(Long userId, Long productId);
    
    /**
     * 分页查询商品列表
     * @param queryParams 查询参数
     * @return 分页结果
     */
    PageResult<ProductPageVO> pageProducts(ProductPageQueryDao queryParams);
}