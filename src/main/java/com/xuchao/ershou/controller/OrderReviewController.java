package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.JwtUtil;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dto.OrderReviewRequest;
import com.xuchao.ershou.model.vo.OrderReviewVO;
import com.xuchao.ershou.service.OrderReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单评价控制器
 */
@RestController
@RequestMapping("/order/review")
public class OrderReviewController {
    
    @Autowired
    private OrderReviewService orderReviewService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 添加订单评价
     * @param request 评价请求
     * @param httpServletRequest HTTP请求
     * @return 评价信息
     */
    @PostMapping("/add")
    public BaseResponse<OrderReviewVO> addReview(@RequestBody @Valid OrderReviewRequest request, 
                                               HttpServletRequest httpServletRequest) {
        // 验证用户身份
        String token = httpServletRequest.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        
        // 验证当前用户
        Long currentUserId = jwtUtil.getUserIdFromToken(token.substring(7));
        if (!currentUserId.equals(request.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 处理评价
        OrderReviewVO reviewVO = orderReviewService.addReview(request);
        return ResultUtils.success(reviewVO);
    }
    
    /**
     * 根据订单ID获取评价
     * @param orderId 订单ID
     * @return 评价信息
     */
    @GetMapping("/order/{orderId}")
    public BaseResponse<OrderReviewVO> getReviewByOrderId(@PathVariable Long orderId) {
        OrderReviewVO reviewVO = orderReviewService.getReviewByOrderId(orderId);
        return ResultUtils.success(reviewVO);
    }
    
    /**
     * 根据商品ID获取评价列表
     * @param productId 商品ID
     * @return 评价列表
     */
    @GetMapping("/product/{productId}")
    public BaseResponse<List<OrderReviewVO>> getReviewsByProductId(@PathVariable Long productId) {
        List<OrderReviewVO> reviewVOList = orderReviewService.getReviewsByProductId(productId);
        return ResultUtils.success(reviewVOList);
    }
    
    /**
     * 根据用户ID获取评价列表
     * @param userId 用户ID
     * @return 评价列表
     */
    @GetMapping("/user/{userId}")
    public BaseResponse<List<OrderReviewVO>> getReviewsByUserId(@PathVariable Long userId) {
        List<OrderReviewVO> reviewVOList = orderReviewService.getReviewsByUserId(userId);
        return ResultUtils.success(reviewVOList);
    }
} 