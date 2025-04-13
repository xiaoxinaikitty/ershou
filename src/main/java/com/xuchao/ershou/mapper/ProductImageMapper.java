package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.ProductImage;
import org.apache.ibatis.annotations.Param;

/**
 * 商品图片Mapper接口
 */
public interface ProductImageMapper extends BaseMapper<ProductImage> {

    /**
     * 插入商品图片信息
     * @param productImage 商品图片信息
     * @return 影响的行数
     */
    int insertProductImage(ProductImage productImage);
    
    /**
     * 根据商品ID和是否为主图，更新其他图片为非主图
     * @param productId 商品ID
     * @return 影响的行数
     */
    int updateOtherImagesNonMain(@Param("productId") Long productId);
    
    /**
     * 将商品的第一张图片设置为主图（在删除主图后使用）
     * @param productId 商品ID
     * @return 影响的行数
     */
    int setNewMainImage(@Param("productId") Long productId);
}
