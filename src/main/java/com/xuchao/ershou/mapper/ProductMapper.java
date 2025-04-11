package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.Product;
import org.apache.ibatis.annotations.Param;

/**
 * 商品Mapper接口
 */
public interface ProductMapper extends BaseMapper<Product> {
    
    /**
     * 插入商品信息
     * @param product 商品信息
     * @return 影响的行数
     */
    int insertProduct(Product product);
    
    /**
     * 根据ID查询商品详细信息
     * @param productId 商品ID
     * @return 商品信息
     */
    Product selectProductById(@Param("productId") Long productId);
    
    /**
     * 更新商品信息
     * @param product 商品更新信息
     * @return 影响的行数
     */
    int updateProduct(Product product);
}