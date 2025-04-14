package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dao.order.OrderCreateDao;
import com.xuchao.ershou.model.vo.OrderVO;
import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     * @param userId 当前登录用户ID（买家）
     * @param orderCreateDao 订单创建信息
     * @return 创建成功的订单详情
     */
    OrderVO createOrder(Long userId, OrderCreateDao orderCreateDao);

    /**
     * 获取订单列表
     * @param userId 当前登录用户ID
     * @return 用户的订单列表
     */
    List<OrderVO> getOrderList(Long userId);
}