package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.Order;
import org.apache.ibatis.annotations.Param;

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
}