package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.vo.OrderVO;
import com.xuchao.ershou.service.PaymentService;
import com.xuchao.ershou.common.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 支付控制器
 */
@Slf4j
@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取待付款商品列表
     * @param authorization 认证头部(Bearer token)
     * @return 待付款商品列表
     */
    @GetMapping("/pending-payment/list")
    public BaseResponse<List<OrderVO>> getPendingPaymentList(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        
        // 获取用户ID
        Long userId = getUserIdFromAuthorization(authorization);
        
        // 调用服务层获取待付款订单列表
        List<OrderVO> pendingPaymentList = paymentService.getPendingPaymentList(userId);
        
        return ResultUtils.success(pendingPaymentList);
    }

    /**
     * 获取待发货商品列表
     * @param authorization 认证头部(Bearer token)
     * @return 待发货商品列表
     */
    @GetMapping("/waiting-shipment/list")
    public BaseResponse<List<OrderVO>> getWaitingShipmentList(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        
        // 获取用户ID
        Long userId = getUserIdFromAuthorization(authorization);
        
        // 调用服务层获取待发货订单列表
        List<OrderVO> waitingShipmentList = paymentService.getWaitingShipmentList(userId);
        
        return ResultUtils.success(waitingShipmentList);
    }
    
    /**
     * 从认证头部获取用户ID
     * @param authorization 认证头部(Bearer token)
     * @return 用户ID
     */
    private Long getUserIdFromAuthorization(String authorization) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            log.warn("用户未登录，无法获取订单列表");
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        String token = authorization.substring(7);
        
        try {
            // 验证Token有效性
            if (!jwtUtil.validateToken(token)) {
                log.warn("无效的token: {}", token);
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "无效的token，请重新登录");
            }
            
            // 从Token中提取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            log.info("从token中提取到userId: {}", userId);
            
            if (userId == null) {
                log.warn("用户ID为空，无法获取订单列表");
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "无法获取用户信息");
            }
            
            return userId;
        } catch (Exception e) {
            log.error("JWT令牌验证或解析失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Token校验失败，请重新登录");
        }
    }
} 