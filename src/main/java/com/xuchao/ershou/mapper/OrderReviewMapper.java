package com.xuchao.ershou.mapper;

import com.xuchao.ershou.model.entity.OrderReview;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 订单评价Mapper接口
 */
@Mapper
public interface OrderReviewMapper {
    /**
     * 插入评价
     * @param orderReview 评价信息
     * @return 影响行数
     */
    int insertOrderReview(OrderReview orderReview);
    
    /**
     * 根据订单ID查询评价
     * @param orderId 订单ID
     * @return 评价信息
     */
    OrderReview selectByOrderId(Long orderId);
    
    /**
     * 根据商品ID查询评价列表
     * @param productId 商品ID
     * @return 评价列表
     */
    List<OrderReview> selectByProductId(Long productId);
    
    /**
     * 根据用户ID查询评价列表
     * @param userId 用户ID
     * @return 评价列表
     */
    List<OrderReview> selectByUserId(Long userId);
    
    /**
     * 根据评价ID更新评价
     * @param orderReview 评价信息
     * @return 影响行数
     */
    int updateOrderReview(OrderReview orderReview);
} 