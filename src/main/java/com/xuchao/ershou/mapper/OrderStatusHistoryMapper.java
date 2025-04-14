package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.OrderStatusHistory;
import org.apache.ibatis.annotations.Param;

/**
 * 订单状态历史Mapper接口
 */
public interface OrderStatusHistoryMapper extends BaseMapper<OrderStatusHistory> {

    /**
     * 插入订单状态历史
     * @param orderStatusHistory 订单状态历史信息
     * @return 影响的行数
     */
    int insertOrderStatusHistory(OrderStatusHistory orderStatusHistory);
}