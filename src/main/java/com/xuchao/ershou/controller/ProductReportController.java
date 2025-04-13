package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.common.JwtUtil;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dao.product.ProductReportAddDao;
import com.xuchao.ershou.model.vo.ProductReportVO;
import com.xuchao.ershou.service.ProductReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品举报控制器
 */
@RestController
@RequestMapping("/product/report")
public class ProductReportController {
    
    @Autowired
    private ProductReportService productReportService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 举报商品
     * @param productReportAddDao 举报商品信息
     * @param authorization 认证头部(Bearer token)
     * @return 举报结果
     */
    @PostMapping("/add")
    public BaseResponse<ProductReportVO> addProductReport(
            @RequestBody @Valid ProductReportAddDao productReportAddDao,
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
        
        // 5. 调用服务层提交举报
        ProductReportVO productReportVO = productReportService.addProductReport(userId, productReportAddDao);
        
        return ResultUtils.success(productReportVO);
    }
}
