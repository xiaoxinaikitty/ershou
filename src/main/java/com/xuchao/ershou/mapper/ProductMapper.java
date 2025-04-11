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
}