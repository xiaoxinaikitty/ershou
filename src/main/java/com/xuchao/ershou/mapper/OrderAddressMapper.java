package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.OrderAddress;
import org.apache.ibatis.annotations.Param;

/**
 * 订单地址Mapper接口
 */
public interface OrderAddressMapper extends BaseMapper<OrderAddress> {

    /**
     * 插入订单地址
     * @param orderAddress 订单地址信息
     * @return 影响的行数
     */
    int insertOrderAddress(OrderAddress orderAddress);
    
    /**
     * 根据订单ID查询订单地址
     * @param orderId 订单ID
     * @return 订单地址
     */
    OrderAddress selectByOrderId(@Param("orderId") Long orderId);
}