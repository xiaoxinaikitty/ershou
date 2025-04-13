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
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品图片控制器
 */
@RestController
@RequestMapping("/product/image")
public class ProductImageController {
    
    @Autowired
    private ProductImageService productImageService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 添加商品图片
     * @param productImageAddDao 商品图片信息
     * @param authorization 认证头部(Bearer token)
     * @return 处理结果
     */
    @PostMapping("/add")
    public BaseResponse<ProductImageVO> addProductImage(
            @RequestBody @Valid ProductImageAddDao productImageAddDao,
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
        
        // 5. 调用服务层添加商品图片
        ProductImageVO productImageVO = productImageService.addProductImage(userId, productImageAddDao);
        
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
}
