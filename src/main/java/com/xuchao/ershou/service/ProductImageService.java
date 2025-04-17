package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dao.product.ProductImageAddDao;
import com.xuchao.ershou.model.entity.ProductImage;
import com.xuchao.ershou.model.vo.ProductImageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 商品图片服务接口
 */
public interface ProductImageService {
    
    /**
     * 添加商品图片
     * @param userId 当前登录用户ID
     * @param productImageAddDao 商品图片信息
     * @return 添加的商品图片信息
     */
    ProductImageVO addProductImage(Long userId, ProductImageAddDao productImageAddDao);
    
    /**
     * 上传商品图片
     * @param userId 当前登录用户ID
     * @param productImageAddDao 商品图片信息
     * @param imageFile 图片文件
     * @return 添加的商品图片信息
     */
    ProductImageVO uploadProductImage(Long userId, ProductImageAddDao productImageAddDao, MultipartFile imageFile);
    
    /**
     * 通过URL添加商品图片
     * @param userId 当前登录用户ID
     * @param productImageAddDao 商品图片信息（包含图片URL）
     * @return 添加的商品图片信息
     */
    ProductImageVO addProductImageByUrl(Long userId, ProductImageAddDao productImageAddDao);
    
    /**
     * 批量添加商品图片URL
     * @param userId 当前登录用户ID
     * @param productImageAddDao 商品图片信息（包含多个图片URL列表）
     * @return 添加的商品图片信息列表
     */
    List<ProductImageVO> batchAddProductImagesByUrl(Long userId, ProductImageAddDao productImageAddDao);
    
    /**
     * 删除商品图片
     * @param userId 当前登录用户ID
     * @param productId 商品ID
     * @param imageId 图片ID
     * @return 是否删除成功
     */
    boolean deleteProductImage(Long userId, Long productId, Long imageId);
}
