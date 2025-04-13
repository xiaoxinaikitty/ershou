package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.common.JwtUtil;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dao.product.ProductFavoriteAddDao;
import com.xuchao.ershou.model.vo.ProductFavoriteVO;
import com.xuchao.ershou.service.ProductFavoriteService;
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
 * 商品收藏控制器
 */
@RestController
@RequestMapping("/product/favorite")
public class ProductFavoriteController {
    
    @Autowired
    private ProductFavoriteService productFavoriteService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 收藏商品
     * @param productFavoriteAddDao 收藏商品信息
     * @param authorization 认证头部(Bearer token)
     * @return 收藏结果
     */
    @PostMapping("/add")
    public BaseResponse<ProductFavoriteVO> addProductFavorite(
            @RequestBody @Valid ProductFavoriteAddDao productFavoriteAddDao,
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
        
        // 5. 调用服务层收藏商品
        ProductFavoriteVO productFavoriteVO = productFavoriteService.addProductFavorite(userId, productFavoriteAddDao);
        
        return ResultUtils.success(productFavoriteVO);
    }
    
    /**
     * 取消收藏商品
     * @param productId 商品ID
     * @param authorization 认证头部(Bearer token)
     * @return 取消收藏结果
     */
    @DeleteMapping("/{productId}")
    public BaseResponse<String> cancelProductFavorite(
            @PathVariable Long productId,
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
        
        // 5. 调用服务层取消收藏商品
        boolean result = productFavoriteService.cancelProductFavorite(userId, productId);
        
        if (result) {
            return ResultUtils.success("取消收藏成功");
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "取消收藏失败");
        }
    }
}
