package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dto.OrderReviewRequest;
import com.xuchao.ershou.model.vo.OrderReviewVO;
import java.util.List;

/**
 * 订单评价服务接口
 */
public interface OrderReviewService {
    
    /**
     * 添加评价
     * @param request 评价请求
     * @return 评价信息
     */
    OrderReviewVO addReview(OrderReviewRequest request);
    
    /**
     * 根据订单ID获取评价
     * @param orderId 订单ID
     * @return 评价信息
     */
    OrderReviewVO getReviewByOrderId(Long orderId);
    
    /**
     * 根据商品ID获取评价列表
     * @param productId 商品ID
     * @return 评价列表
     */
    List<OrderReviewVO> getReviewsByProductId(Long productId);
    
    /**
     * 根据用户ID获取评价列表
     * @param userId 用户ID
     * @return 评价列表
     */
    List<OrderReviewVO> getReviewsByUserId(Long userId);
} 