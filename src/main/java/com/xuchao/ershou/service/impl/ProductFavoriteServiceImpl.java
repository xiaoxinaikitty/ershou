package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.mapper.ProductFavoriteMapper;
import com.xuchao.ershou.mapper.ProductMapper;
import com.xuchao.ershou.mapper.ProductImageMapper;
import com.xuchao.ershou.model.dao.product.ProductFavoriteAddDao;
import com.xuchao.ershou.model.entity.Product;
import com.xuchao.ershou.model.entity.ProductFavorite;
import com.xuchao.ershou.model.entity.ProductImage;
import com.xuchao.ershou.model.vo.ProductFavoriteVO;
import com.xuchao.ershou.service.ProductFavoriteService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品收藏服务实现类
 */
@Service
public class ProductFavoriteServiceImpl implements ProductFavoriteService {

    @Autowired
    private ProductFavoriteMapper productFavoriteMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private ProductImageMapper productImageMapper;
    
    @Override
    @Transactional
    public ProductFavoriteVO addProductFavorite(Long userId, ProductFavoriteAddDao productFavoriteAddDao) {
        // 参数校验
        if (productFavoriteAddDao == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "收藏商品信息不能为空");
        }
        
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        Long productId = productFavoriteAddDao.getProductId();
        
        // 检查商品是否存在
        Product product = productMapper.selectProductById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        
        // 检查是否已收藏
        int existCount = productFavoriteMapper.checkUserFavorite(userId, productId);
        if (existCount > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您已收藏该商品，请勿重复收藏");
        }
        
        // 创建收藏记录
        ProductFavorite productFavorite = new ProductFavorite();
        productFavorite.setProductId(productId);
        productFavorite.setUserId(userId);
        
        // 插入数据库
        int result = productFavoriteMapper.insertProductFavorite(productFavorite);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "收藏商品失败");
        }
        
        // 构建返回数据
        ProductFavoriteVO favoriteVO = new ProductFavoriteVO();
        BeanUtils.copyProperties(productFavorite, favoriteVO);
        
        // 填充商品信息
        favoriteVO.setProductTitle(product.getTitle());
        favoriteVO.setProductPrice(product.getPrice().toString());
        
        // 获取商品主图
        QueryWrapper<ProductImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId).eq("is_main", 1);
        ProductImage mainImage = productImageMapper.selectOne(queryWrapper);
        if (mainImage != null) {
            favoriteVO.setProductImage(mainImage.getImageUrl());
        }
        
        return favoriteVO;
    }
      @Override
    @Transactional
    public boolean cancelProductFavorite(Long userId, Long productId) {
        // 参数校验
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品ID无效");
        }
        
        // 检查商品是否存在
        Product product = productMapper.selectProductById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        
        // 检查是否已收藏
        int existCount = productFavoriteMapper.checkUserFavorite(userId, productId);
        if (existCount <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您尚未收藏该商品");
        }
        
        // 执行取消收藏
        int result = productFavoriteMapper.deleteProductFavorite(userId, productId);
        
        return result > 0;
    }
    
    @Override
    public List<ProductFavoriteVO> getFavoriteList(Long userId) {
        // 参数校验
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 直接调用Mapper查询收藏列表
        List<ProductFavoriteVO> favoriteList = productFavoriteMapper.selectFavoriteByUserId(userId);
        
        return favoriteList;
    }
    
    @Override
    public boolean checkUserFavorite(Long userId, Long productId) {
        // 参数校验
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品ID无效");
        }
        
        // 调用Mapper检查是否已收藏
        int existCount = productFavoriteMapper.checkUserFavorite(userId, productId);
        
        return existCount > 0;
    }
}
