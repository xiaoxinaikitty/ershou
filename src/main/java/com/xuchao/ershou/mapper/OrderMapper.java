package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.Order;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 订单Mapper接口
 */
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 插入订单
     * @param order 订单信息
     * @return 影响的行数
     */
    int insertOrder(Order order);
    
    /**
     * 根据订单ID查询订单详情
     * @param orderId 订单ID
     * @return 订单信息
     */
    Order selectOrderById(@Param("orderId") Long orderId);

    /**
     * 根据用户ID查询订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> selectOrdersByUserId(@Param("userId") Long userId);
    
    /**
     * 更新订单信息
     * @param order 订单更新信息
     * @return 影响的行数
     */
    int updateOrder(Order order);
}