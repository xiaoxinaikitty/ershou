package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.common.JwtUtil;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dao.product.RecycleMethodAddDao;
import com.xuchao.ershou.model.vo.RecycleMethodVO;
import com.xuchao.ershou.service.RecycleMethodService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 商品交易方式控制器
 */
@RestController
@RequestMapping("/product/trade")
public class RecycleMethodController {
    
    @Autowired
    private RecycleMethodService recycleMethodService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 添加商品交易方式
     * @param methodAddDao 交易方式信息
     * @param authorization 认证头部(Bearer token)
     * @return 添加的交易方式信息
     */
    @PostMapping("/method/add")
    public BaseResponse<RecycleMethodVO> addRecycleMethod(
            @RequestBody @Valid RecycleMethodAddDao methodAddDao,
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
        
        // 5. 调用服务层添加交易方式
        RecycleMethodVO methodVO = recycleMethodService.addRecycleMethod(userId, methodAddDao);
        
        return ResultUtils.success(methodVO);
    }
}