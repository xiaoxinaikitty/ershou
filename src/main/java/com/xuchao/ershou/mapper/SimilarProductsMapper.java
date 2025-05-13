package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.SimilarProducts;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 相似商品Mapper接口
 */
@Mapper
public interface SimilarProductsMapper extends BaseMapper<SimilarProducts> {
    
    /**
     * 根据商品ID查询相似商品列表
     * 
     * @param productId 商品ID
     * @param limit 限制数量
     * @return 相似商品ID列表
     */
    @Select("SELECT similar_product_id FROM similar_products WHERE product_id = #{productId} ORDER BY similarity_score DESC LIMIT #{limit}")
    List<Long> findSimilarProductIds(@Param("productId") Long productId, @Param("limit") Integer limit);
} 