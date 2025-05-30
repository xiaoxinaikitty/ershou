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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public BaseResponse<Boolean> cancelProductFavorite(
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
        
        return ResultUtils.success(result);
    }
    
    /**
     * 获取用户收藏的商品列表
     * @param authorization 认证头部(Bearer token)
     * @return 收藏商品列表
     */
    @GetMapping("/list")
    public BaseResponse<List<ProductFavoriteVO>> getFavoriteList(
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
        
        // 5. 调用服务层获取收藏列表
        List<ProductFavoriteVO> favoriteList = productFavoriteService.getFavoriteList(userId);
        
        return ResultUtils.success(favoriteList);
    }
    
    /**
     * 检查用户是否已收藏商品
     * @param productId 商品ID
     * @param authorization 认证头部(Bearer token)
     * @return 是否已收藏
     */
    @GetMapping("/check/{productId}")
    public BaseResponse<Map<String, Boolean>> checkFavorite(
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
        
        // 5. 调用服务层检查是否已收藏
        boolean isFavorite = productFavoriteService.checkUserFavorite(userId, productId);
        
        // 6. 构建返回结果
        Map<String, Boolean> result = new HashMap<>();
        result.put("isFavorite", isFavorite);
        
        return ResultUtils.success(result);
    }
}
