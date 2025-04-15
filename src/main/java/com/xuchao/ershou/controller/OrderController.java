package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.common.JwtUtil;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dao.order.OrderCancelDao;
import com.xuchao.ershou.model.dao.order.OrderCreateDao;
import com.xuchao.ershou.model.dto.OrderPayRequest;
import com.xuchao.ershou.model.vo.OrderVO;
import com.xuchao.ershou.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 创建订单
     * @param orderCreateDao 订单创建信息
     * @param authorization 认证头部(Bearer token)
     * @return 创建的订单信息
     */
    @PostMapping("/create")
    public BaseResponse<OrderVO> createOrder(
            @RequestBody @Valid OrderCreateDao orderCreateDao,
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
        
        // 5. 调用服务层创建订单
        OrderVO orderVO = orderService.createOrder(userId, orderCreateDao);
        
        return ResultUtils.success(orderVO);
    }

    /**
     * 获取订单列表
     * @param authorization 认证头部(Bearer token)
     * @return 用户的订单列表
     */
    @GetMapping("/list")
    public BaseResponse<List<OrderVO>> getOrderList(
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

        // 5. 调用服务层获取订单列表
        List<OrderVO> orderList = orderService.getOrderList(userId);

        return ResultUtils.success(orderList);
    }
    
    /**
     * 取消订单
     * @param orderCancelDao 订单取消信息
     * @param authorization 认证头部(Bearer token)
     * @return 取消后的订单信息
     */
    @PutMapping("/cancel")
    public BaseResponse<OrderVO> cancelOrder(
            @RequestBody @Valid OrderCancelDao orderCancelDao,
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
        
        // 5. 调用服务层取消订单
        OrderVO orderVO = orderService.cancelOrder(userId, orderCancelDao);
        
        return ResultUtils.success(orderVO);
    }

    @PutMapping("/pay")
    public BaseResponse<OrderVO> payOrder(@RequestBody OrderPayRequest request, HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        
        // 验证当前用户
        Long currentUserId = jwtUtil.getUserIdFromToken(token.substring(7));
        if (!currentUserId.equals(request.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        return ResultUtils.success(orderService.payOrder(request));
    }
}