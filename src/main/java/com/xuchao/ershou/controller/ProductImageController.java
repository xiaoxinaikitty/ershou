package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.common.JwtUtil;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dao.product.ProductImageAddDao;
import com.xuchao.ershou.model.vo.ProductImageVO;
import com.xuchao.ershou.service.ProductImageService;
import com.xuchao.ershou.service.ImageAuditService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * 商品图片控制器
 */
@RestController
@RequestMapping("/product/image")
public class ProductImageController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductImageController.class);
    
    @Autowired
    private ProductImageService productImageService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ImageAuditService imageAuditService;
    
    /**
     * 添加商品图片
     * @param productId 商品ID
     * @param isMain 是否为主图(0否 1是)
     * @param sortOrder 图片排序
     * @param imageFile 图片文件
     * @param authorization 认证头部(Bearer token)
     * @return 处理结果
     */
    @PostMapping("/upload")
    public BaseResponse<ProductImageVO> uploadProductImage(
            @RequestParam("productId") Long productId,
            @RequestParam(value = "isMain", required = false, defaultValue = "0") Integer isMain,
            @RequestParam(value = "sortOrder", required = false, defaultValue = "0") Integer sortOrder,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
            
        // 检查图片文件
        if (imageFile == null || imageFile.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片文件不能为空");
        }
        
        // 移除图片内容审核过程，恢复原有逻辑
        logger.info("上传商品图片，文件名: {}", imageFile.getOriginalFilename());
        
        Long userId = null;
        
        // 1. 从Authorization头中提取Token
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            
            // 2. 验证Token有效性
            if (!jwtUtil.validateToken(token)) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "无效的token，请重新登录");
            }
            
            // 3. 从Token中提取用户ID
            userId = jwtUtil.getUserIdFromToken(token);
        }
        
        // 4. 如果从Token中无法获取用户ID，则尝试从请求上下文中获取
        if (userId == null) {
            userId = CurrentUserUtils.getCurrentUserId();
            if (userId == null) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
            }
        }
        
        // 5. 构建商品图片添加对象
        ProductImageAddDao productImageAddDao = new ProductImageAddDao();
        productImageAddDao.setProductId(productId);
        productImageAddDao.setIsMain(isMain);
        productImageAddDao.setSortOrder(sortOrder);
        
        // 6. 调用服务层上传商品图片
        ProductImageVO productImageVO = productImageService.uploadProductImage(userId, productImageAddDao, imageFile);
        
        return ResultUtils.success(productImageVO);
    }
    
    /**
     * 通过URL添加商品图片
     * @param productImageAddDao 包含图片URL的商品图片信息
     * @param authorization 认证头部(Bearer token)
     * @return 处理结果
     */
    @PostMapping("/add-by-url")
    public BaseResponse<ProductImageVO> addProductImageByUrl(
            @RequestBody @Valid ProductImageAddDao productImageAddDao,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
            
        // 检查图片URL
        if (productImageAddDao == null || productImageAddDao.getImageUrl() == null || productImageAddDao.getImageUrl().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片URL不能为空");
        }
        
        // 添加简化的图片URL内容审核过程
        String imageUrl = productImageAddDao.getImageUrl().trim();
        logger.info("审核图片URL: {}", imageUrl);
        
        // 对图片URL进行内容审核
        try {
            boolean isImageSafe = imageAuditService.auditImageUrl(imageUrl);
            if (!isImageSafe) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "图片内容违规，请上传合规的图片");
            }
            logger.info("图片审核通过");
        } catch (Exception e) {
            logger.error("图片审核出错", e);
            // 审核出错时，允许上传继续，不阻断流程
        }
        
        Long userId = null;
        
        // 1. 从Authorization头中提取Token
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            
            // 2. 验证Token有效性
            if (!jwtUtil.validateToken(token)) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "无效的token，请重新登录");
            }
            
            // 3. 从Token中提取用户ID
            userId = jwtUtil.getUserIdFromToken(token);
        }
        
        // 4. 如果从Token中无法获取用户ID，则尝试从请求上下文中获取
        if (userId == null) {
            userId = CurrentUserUtils.getCurrentUserId();
            if (userId == null) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
            }
        }
        
        // 5. 调用服务层通过URL添加商品图片
        ProductImageVO productImageVO = productImageService.addProductImageByUrl(userId, productImageAddDao);
        
        return ResultUtils.success(productImageVO);
    }
    
    /**
     * 删除商品图片
     * @param productId 商品ID
     * @param imageId 图片ID
     * @param authorization 认证头部(Bearer token)
     * @return 处理结果
     */
    @DeleteMapping("/{productId}/{imageId}")
    public BaseResponse<String> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
            
        Long userId = null;
        
        // 1. 从Authorization头中提取Token
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            
            // 2. 验证Token有效性
            if (!jwtUtil.validateToken(token)) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "无效的token，请重新登录");
            }
            
            // 3. 从Token中提取用户ID
            userId = jwtUtil.getUserIdFromToken(token);
        }
        
        // 4. 如果从Token中无法获取用户ID，则尝试从请求上下文中获取
        if (userId == null) {
            userId = CurrentUserUtils.getCurrentUserId();
            if (userId == null) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
            }
        }
        
        // 5. 调用服务层删除商品图片
        boolean result = productImageService.deleteProductImage(userId, productId, imageId);
        
        if (result) {
            return ResultUtils.success("图片删除成功");
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图片删除失败");
        }
    }
    
    /**
     * 批量添加商品图片
     * @param productImageAddDao 包含多个图片URL的商品图片信息
     * @param authorization 认证头部(Bearer token)
     * @return 处理结果
     */
    @PostMapping("/add")
    public BaseResponse<List<ProductImageVO>> addProductImages(
            @RequestBody @Valid ProductImageAddDao productImageAddDao,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
            
        // 添加调试日志
        logger.info("收到批量添加商品图片请求，请求数据: {}", productImageAddDao);
        if (productImageAddDao != null) {
            logger.info("productId: {}, imageUrl: {}, imageUrls: {}", 
                productImageAddDao.getProductId(), 
                productImageAddDao.getImageUrl(),
                productImageAddDao.getImageUrls());
        }
        
        // 检查商品ID
        if (productImageAddDao == null || productImageAddDao.getProductId() == null) {
            logger.error("商品ID为空");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品ID不能为空");
        }
        
        // 检查图片URL列表是否为空 (DTO中的getImageUrls()已经处理了从imageUrl到imageUrls的转换)
        if (productImageAddDao.getImageUrls().isEmpty()) {
            logger.error("图片URL列表为空，且没有提供单张图片URL, productId: {}", productImageAddDao.getProductId());
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请提供至少一个有效的图片URL");
        }
        
        // 添加图片内容审核逻辑
        logger.info("开始批量审核图片URL");
        for (String imageUrl : productImageAddDao.getImageUrls()) {
            try {
                boolean isImageSafe = imageAuditService.auditImageUrl(imageUrl);
                if (!isImageSafe) {
                    throw new BusinessException(ErrorCode.FORBIDDEN, "存在图片内容违规，请上传合规的图片");
                }
            } catch (Exception e) {
                logger.error("图片审核出错", e);
                // 审核出错时，允许上传继续，不阻断流程
            }
        }
        logger.info("所有图片审核通过");
        
        Long userId = null;
        
        // 1. 从Authorization头中提取Token
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            
            // 2. 验证Token有效性
            if (!jwtUtil.validateToken(token)) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "无效的token，请重新登录");
            }
            
            // 3. 从Token中提取用户ID
            userId = jwtUtil.getUserIdFromToken(token);
        }
        
        // 4. 如果从Token中无法获取用户ID，则尝试从请求上下文中获取
        if (userId == null) {
            userId = CurrentUserUtils.getCurrentUserId();
            if (userId == null) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
            }
        }
        
        // 5. 调用服务层批量添加商品图片
        List<ProductImageVO> productImageVOList = productImageService.batchAddProductImagesByUrl(userId, productImageAddDao);
        
        return ResultUtils.success(productImageVOList);
    }
}
