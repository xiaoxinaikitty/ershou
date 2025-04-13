package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.ProductFavorite;
import com.xuchao.ershou.model.vo.ProductFavoriteVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品收藏Mapper接口
 */
public interface ProductFavoriteMapper extends BaseMapper<ProductFavorite> {
    
    /**
     * 检查用户是否已收藏商品
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 收藏记录数
     */
    int checkUserFavorite(@Param("userId") Long userId, @Param("productId") Long productId);
    
    /**
     * 添加商品收藏
     * @param productFavorite 商品收藏信息
     * @return 影响的行数
     */
    int insertProductFavorite(ProductFavorite productFavorite);
    
    /**
     * 取消商品收藏
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 影响的行数
     */
    int deleteProductFavorite(@Param("userId") Long userId, @Param("productId") Long productId);
    
    /**
     * 查询用户收藏的商品列表
     * @param userId 用户ID
     * @return 收藏商品列表，包含商品信息
     */
    List<ProductFavoriteVO> selectFavoriteByUserId(@Param("userId") Long userId);
}
