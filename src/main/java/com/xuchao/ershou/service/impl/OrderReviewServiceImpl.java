package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.mapper.OrderMapper;
import com.xuchao.ershou.mapper.OrderReviewMapper;
import com.xuchao.ershou.mapper.ProductMapper;
import com.xuchao.ershou.mapper.UserMapper;
import com.xuchao.ershou.model.dto.OrderReviewRequest;
import com.xuchao.ershou.model.entity.Order;
import com.xuchao.ershou.model.entity.OrderReview;
import com.xuchao.ershou.model.entity.Product;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.model.vo.OrderReviewVO;
import com.xuchao.ershou.service.OrderReviewService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单评价服务实现类
 */
@Service
public class OrderReviewServiceImpl implements OrderReviewService {

    @Autowired
    private OrderReviewMapper orderReviewMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderReviewVO addReview(OrderReviewRequest request) {
        // 1. 参数校验
        if (request == null || request.getOrderId() == null || request.getProductId() == null 
                || request.getUserId() == null || request.getSellerId() == null
                || request.getContent() == null || request.getRating() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 评分范围校验
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评分必须在1-5之间");
        }
        
        // 2. 获取订单信息并验证
        Order order = orderMapper.selectOrderById(request.getOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }
        
        // 3. 验证订单状态是否已完成
        if (order.getOrderStatus() != 3) { // 3表示已完成
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "只能评价已完成的订单");
        }
        
        // 4. 验证订单所属用户是否匹配
        if (!order.getUserId().equals(request.getUserId()) || !order.getSellerId().equals(request.getSellerId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权评价此订单");
        }
        
        // 5. 检查是否已评价
        OrderReview existingReview = orderReviewMapper.selectByOrderId(request.getOrderId());
        if (existingReview != null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该订单已评价");
        }
        
        // 6. 创建评价对象
        OrderReview orderReview = new OrderReview();
        BeanUtils.copyProperties(request, orderReview);
        orderReview.setHasReply(0); // 初始未回复
        orderReview.setCreatedTime(LocalDateTime.now());
        
        // 7. 保存评价
        int result = orderReviewMapper.insertOrderReview(orderReview);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评价保存失败");
        }
        
        // 8. 返回评价信息
        return convertToVO(orderReview);
    }
    
    @Override
    public OrderReviewVO getReviewByOrderId(Long orderId) {
        if (orderId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        OrderReview review = orderReviewMapper.selectByOrderId(orderId);
        if (review == null) {
            return null;
        }
        
        return convertToVO(review);
    }
    
    @Override
    public List<OrderReviewVO> getReviewsByProductId(Long productId) {
        if (productId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        List<OrderReview> reviews = orderReviewMapper.selectByProductId(productId);
        if (reviews == null || reviews.isEmpty()) {
            return new ArrayList<>();
        }
        
        return reviews.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    @Override
    public List<OrderReviewVO> getReviewsByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        List<OrderReview> reviews = orderReviewMapper.selectByUserId(userId);
        if (reviews == null || reviews.isEmpty()) {
            return new ArrayList<>();
        }
        
        return reviews.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    /**
     * 将评价实体转换为VO
     */
    private OrderReviewVO convertToVO(OrderReview review) {
        OrderReviewVO vo = new OrderReviewVO();
        BeanUtils.copyProperties(review, vo);
        
        // 查询商品标题
        try {
            Product product = productMapper.selectProductById(review.getProductId());
            if (product != null) {
                vo.setProductTitle(product.getTitle());
            }
        } catch (Exception e) {
            // 忽略异常，继续处理
        }
        
        // 处理用户信息（匿名则不显示用户名）
        if (review.getIsAnonymous() == 0) {
            try {
                User user = userMapper.selectById(review.getUserId());
                if (user != null) {
                    vo.setUsername(user.getUsername());
                }
            } catch (Exception e) {
                // 忽略异常，继续处理
            }
        } else {
            vo.setUsername("匿名用户");
        }
        
        // 查询卖家用户名
        try {
            User seller = userMapper.selectById(review.getSellerId());
            if (seller != null) {
                vo.setSellerName(seller.getUsername());
            }
        } catch (Exception e) {
            // 忽略异常，继续处理
        }
        
        return vo;
    }
} 