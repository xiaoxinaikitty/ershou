package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dao.product.ProductFavoriteAddDao;
import com.xuchao.ershou.model.vo.ProductFavoriteVO;

import java.util.List;

/**
 * 商品收藏服务接口
 */
public interface ProductFavoriteService {
    
    /**
     * 添加商品收藏
     * @param userId 当前登录用户ID
     * @param productFavoriteAddDao 收藏商品信息
     * @return 收藏结果
     */
    ProductFavoriteVO addProductFavorite(Long userId, ProductFavoriteAddDao productFavoriteAddDao);
    
    /**
     * 取消商品收藏
     * @param userId 当前登录用户ID
     * @param productId 商品ID
     * @return 是否取消成功
     */
    boolean cancelProductFavorite(Long userId, Long productId);
    
    /**
     * 查询用户收藏的商品列表
     * @param userId 用户ID
     * @return 收藏商品列表
     */
    List<ProductFavoriteVO> getFavoriteList(Long userId);
    
    /**
     * 检查用户是否已收藏商品
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否已收藏
     */
    boolean checkUserFavorite(Long userId, Long productId);
}
                                                                                