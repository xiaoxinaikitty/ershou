package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.dao.product.ProductPageQueryDao;
import com.xuchao.ershou.model.dao.product.ProductSearchDao;
import com.xuchao.ershou.model.entity.Product;
import com.xuchao.ershou.model.vo.ProductPageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
    
    /**
     * 分页查询商品列表
     * @param queryParams 查询参数
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 商品列表
     */
    List<ProductPageVO> selectProductPage(
            @Param("query") ProductPageQueryDao queryParams,
            @Param("offset") int offset,
            @Param("limit") int limit);
    
    /**
     * 根据条件统计商品总数
     * @param queryParams 查询参数
     * @return 符合条件的商品总数
     */
    long countProducts(@Param("query") ProductPageQueryDao queryParams);
    
    /**
     * 搜索商品列表（根据标题和描述搜索）
     * @param searchParams 搜索参数
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 商品列表
     */
    List<ProductPageVO> searchProducts(
            @Param("search") ProductSearchDao searchParams,
            @Param("offset") int offset,
            @Param("limit") int limit);
            
    /**
     * 统计搜索结果的商品总数
     * @param searchParams 搜索参数
     * @return 符合搜索条件的商品总数
     */
    long countSearchProducts(@Param("search") ProductSearchDao searchParams);
}