package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.mapper.ProductImageMapper;
import com.xuchao.ershou.mapper.ProductMapper;
import com.xuchao.ershou.model.dao.product.ProductImageAddDao;
import com.xuchao.ershou.model.entity.Product;
import com.xuchao.ershou.model.entity.ProductImage;
import com.xuchao.ershou.model.vo.ProductImageVO;
import com.xuchao.ershou.service.ProductImageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品图片服务实现类
 */
@Service
public class ProductImageServiceImpl implements ProductImageService {

    @Autowired
    private ProductImageMapper productImageMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    @Transactional
    public ProductImageVO addProductImage(Long userId, ProductImageAddDao productImageAddDao) {
        // 参数校验
        if (productImageAddDao == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品图片信息不能为空");
        }
        
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        Long productId = productImageAddDao.getProductId();
        
        // 查询商品是否存在
        Product product = productMapper.selectProductById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        
        // 校验当前用户是否为商品发布者
        if (!product.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限为他人发布的商品添加图片");
        }
        
        // 创建商品图片对象
        ProductImage productImage = new ProductImage();
        BeanUtils.copyProperties(productImageAddDao, productImage);
        
        // 如果当前图片是主图，则需要将该商品的其他图片设置为非主图
        if (productImage.getIsMain() != null && productImage.getIsMain() == 1) {
            productImageMapper.updateOtherImagesNonMain(productId);
        }
        
        // 插入数据库
        int result = productImageMapper.insertProductImage(productImage);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图片添加失败");
        }
        
        // 转换为视图对象返回
        ProductImageVO productImageVO = new ProductImageVO();
        BeanUtils.copyProperties(productImage, productImageVO);
        
        return productImageVO;
    }
}
