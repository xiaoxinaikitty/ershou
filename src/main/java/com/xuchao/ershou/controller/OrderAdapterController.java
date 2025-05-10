package com.xuchao.ershou.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.common.JwtUtil;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.mapper.UserAddressMapper;
import com.xuchao.ershou.model.dao.order.OrderAddressDao;
import com.xuchao.ershou.model.dto.OrderCreateRequest;
import com.xuchao.ershou.model.entity.UserAddress;
import com.xuchao.ershou.model.vo.OrderVO;
import com.xuchao.ershou.service.OrderService;
import com.xuchao.ershou.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单适配器控制器
 * 用于处理前端适配的接口
 */
@Slf4j
@RestController
@RequestMapping("/order/v2")
public class OrderAdapterController {

    @Resource
    private OrderService orderService;

    @Resource
    private UserAddressMapper userAddressMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 创建订单 - 适配前端请求
     * @param request 请求
     * @return 订单信息
     */
    @PostMapping("/create")
    public BaseResponse<Map<String, Object>> createOrder(@RequestBody OrderCreateRequest request, HttpServletRequest httpRequest) {
        // 获取用户ID
        Integer userIdInteger = jwtUtils.getUserId(httpRequest);
        if (userIdInteger == null) {
            // 尝试使用JwtUtil从Authorization头获取
            String token = httpRequest.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                try {
                    Long userIdLong = jwtUtil.getUserIdFromToken(token.substring(7));
                    if (userIdLong != null) {
                        userIdInteger = userIdLong.intValue();
                    }
                } catch (Exception e) {
                    log.error("解析JWT令牌失败", e);
                }
            }
            
            if (userIdInteger == null) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录或登录已过期");
            }
        }
        
        Long userId = userIdInteger.longValue();
        log.info("接收到订单创建请求: {}, 用户ID: {}", request, userId);
        
        UserAddress userAddress = null;
        
        // 验证是否需要收货地址
        // 线下交易(paymentType=2)必须验证地址
        // 在线支付(paymentType=1)可以不需要地址
        if (request.getPaymentType() != null && request.getPaymentType() == 2) {
            // 线下交易必须有收货地址
            if (request.getAddressId() == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "线下交易必须提供收货地址");
            }
            
            userAddress = userAddressMapper.selectById(request.getAddressId());
            if (userAddress == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "收货地址不存在");
            }
        } else {
            // 在线支付，尝试获取地址，没有则使用默认空地址
            if (request.getAddressId() != null) {
                userAddress = userAddressMapper.selectById(request.getAddressId());
            }
        }
        
        // 转换请求
        try {
            // 创建订单
            OrderVO orderVO = orderService.createOrder(userId, request.toOrderCreateDao(userId, userAddress));
            
            // 构建响应数据
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", orderVO.getOrderId());
            result.put("orderNo", orderVO.getOrderNo());
            
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("创建订单异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建订单失败: " + e.getMessage());
        }
    }
} 