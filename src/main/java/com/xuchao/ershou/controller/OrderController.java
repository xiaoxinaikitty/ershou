package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.*;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dao.order.OrderCancelDao;
import com.xuchao.ershou.model.dao.order.OrderCreateDao;
import com.xuchao.ershou.model.dto.OrderConfirmReceiptRequest;
import com.xuchao.ershou.model.dto.OrderNotifyShipmentRequest;
import com.xuchao.ershou.model.dto.OrderPayRequest;
import com.xuchao.ershou.model.vo.OrderVO;
import com.xuchao.ershou.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 订单控制器
 */
@Slf4j
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
            
            try {
                // 2. 验证Token有效性
                if (!jwtUtil.validateToken(token)) {
                    log.warn("无效的token: {}", token);
                    throw new BusinessException(ErrorCode.UNAUTHORIZED, "无效的token，请重新登录");
                }
                
                // 3. 从Token中提取用户ID
                userId = jwtUtil.getUserIdFromToken(token);
                log.info("从token中提取到userId: {}", userId);
            } catch (Exception e) {
                log.error("JWT令牌验证或解析失败: {}", e.getMessage(), e);
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "Token校验失败，请重新登录");
            }
        }
        
        // 4. 如果从Token中无法获取用户ID，则尝试从请求上下文中获取
        if (userId == null) {
            userId = CurrentUserUtils.getCurrentUserId();
            if (userId == null) {
                log.warn("用户未登录，无法创建订单");
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

            try {
                // 2. 验证Token有效性
                if (!jwtUtil.validateToken(token)) {
                    log.warn("无效的token: {}", token);
                    throw new BusinessException(ErrorCode.UNAUTHORIZED, "无效的token，请重新登录");
                }
                
                // 3. 从Token中提取用户ID
                userId = jwtUtil.getUserIdFromToken(token);
                log.info("从token中提取到userId: {}", userId);
            } catch (Exception e) {
                log.error("JWT令牌验证或解析失败: {}", e.getMessage(), e);
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "Token校验失败，请重新登录");
            }
        }

        // 4. 如果从Token中无法获取用户ID，则尝试从请求上下文中获取
        if (userId == null) {
            userId = CurrentUserUtils.getCurrentUserId();
            if (userId == null) {
                log.warn("用户未登录，无法获取订单列表");
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
            
            try {
                // 2. 验证Token有效性
                if (!jwtUtil.validateToken(token)) {
                    log.warn("无效的token: {}", token);
                    throw new BusinessException(ErrorCode.UNAUTHORIZED, "无效的token，请重新登录");
                }
                
                // 3. 从Token中提取用户ID
                userId = jwtUtil.getUserIdFromToken(token);
                log.info("从token中提取到userId: {}", userId);
            } catch (Exception e) {
                log.error("JWT令牌验证或解析失败: {}", e.getMessage(), e);
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "Token校验失败，请重新登录");
            }
        }
        
        // 4. 如果从Token中无法获取用户ID，则尝试从请求上下文中获取
        if (userId == null) {
            userId = CurrentUserUtils.getCurrentUserId();
            if (userId == null) {
                log.warn("用户未登录，无法取消订单");
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
            log.warn("支付订单失败：用户未登录");
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        
        try {
            // 验证当前用户
            Long currentUserId = jwtUtil.getUserIdFromToken(token.substring(7));
            if (currentUserId == null) {
                log.warn("支付订单失败：无法从token中获取用户ID");
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "无法验证用户身份");
            }
            
            if (!currentUserId.equals(request.getUserId())) {
                log.warn("支付订单失败：用户ID不匹配，token用户ID={}, 请求用户ID={}", currentUserId, request.getUserId());
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            
            return ResultUtils.success(orderService.payOrder(request));
        } catch (Exception e) {
            log.error("支付订单异常: {}", e.getMessage(), e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "支付订单失败: " + e.getMessage());
        }
    }

    @PutMapping("/confirm-receipt")
    public BaseResponse<OrderVO> confirmReceipt(@RequestBody OrderConfirmReceiptRequest request, HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("确认收货失败：用户未登录");
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        
        try {
            // 验证当前用户
            Long currentUserId = jwtUtil.getUserIdFromToken(token.substring(7));
            if (currentUserId == null) {
                log.warn("确认收货失败：无法从token中获取用户ID");
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "无法验证用户身份");
            }
            
            if (!currentUserId.equals(request.getUserId())) {
                log.warn("确认收货失败：用户ID不匹配，token用户ID={}, 请求用户ID={}", currentUserId, request.getUserId());
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            
            return ResultUtils.success(orderService.confirmReceipt(request));
        } catch (Exception e) {
            log.error("确认收货异常: {}", e.getMessage(), e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "确认收货失败: " + e.getMessage());
        }
    }

    @PutMapping("/notify-shipment")
    public BaseResponse<OrderVO> notifyShipment(@RequestBody OrderNotifyShipmentRequest request, HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("通知发货失败：用户未登录");
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        
        try {
            // 验证当前用户
            Long currentUserId = jwtUtil.getUserIdFromToken(token.substring(7));
            if (currentUserId == null) {
                log.warn("通知发货失败：无法从token中获取用户ID");
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "无法验证用户身份");
            }
            
            if (!currentUserId.equals(request.getSellerId())) {
                log.warn("通知发货失败：用户ID不匹配，token用户ID={}, 请求卖家ID={}", currentUserId, request.getSellerId());
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            
            return ResultUtils.success(orderService.notifyShipment(request));
        } catch (Exception e) {
            log.error("通知发货异常: {}", e.getMessage(), e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "通知发货失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前登录用户ID
     * @return 用户ID，未登录返回null
     */
    private Long getUserId() {
        // 尝试从请求上下文中获取用户ID
        Long userId = CurrentUserUtils.getCurrentUserId();
        
        // 如果无法获取，尝试从请求头中获取token
        if (userId == null && RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("Authorization");
            
            if (token != null && token.startsWith("Bearer ")) {
                try {
                    // 验证Token有效性
                    token = token.substring(7);
                    if (jwtUtil.validateToken(token)) {
                        userId = jwtUtil.getUserIdFromToken(token);
                    }
                } catch (Exception e) {
                    log.error("JWT令牌验证或解析失败: {}", e.getMessage(), e);
                }
            }
        }
        
        return userId;
    }

    /**
     * 获取当前用户各状态的订单数量
     */
    @GetMapping("/count")
    public BaseResponse<Map<String, Integer>> getOrderCount() {
        // 获取当前登录用户信息
        Long userId = getUserId();
        if (userId == null) {
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR.getCode(), "用户未登录");
        }
        
        try {
            // 获取各状态订单数量
            int pendingPayment = orderService.countOrdersByStatus(userId, 0); // 待付款
            int pendingShipment = orderService.countOrdersByStatus(userId, 1); // 待发货
            int pendingReceipt = orderService.countOrdersByStatus(userId, 2); // 待收货
            int completed = orderService.countOrdersByStatus(userId, 3); // 已完成
            int cancelled = orderService.countOrdersByStatus(userId, 4); // 已取消
            int afterSale = orderService.countOrdersByStatus(userId, 5); // 售后中
            int total = pendingPayment + pendingShipment + pendingReceipt + completed + cancelled + afterSale;
            
            // 构建返回结果
            Map<String, Integer> countMap = new HashMap<>();
            countMap.put("pendingPayment", pendingPayment);
            countMap.put("pendingShipment", pendingShipment);
            countMap.put("pendingReceipt", pendingReceipt);
            countMap.put("completed", completed);
            countMap.put("cancelled", cancelled);
            countMap.put("afterSale", afterSale);
            countMap.put("total", total);
            
            return ResultUtils.success(countMap);
        } catch (Exception e) {
            log.error("获取订单数量统计失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR.getCode(), "获取订单数量统计失败");
        }
    }
}