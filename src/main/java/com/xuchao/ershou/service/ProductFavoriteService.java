package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dao.product.ProductFavoriteAddDao;
import com.xuchao.ershou.model.vo.ProductFavoriteVO;

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
}
