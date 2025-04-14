package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dao.order.OrderCreateDao;
import com.xuchao.ershou.model.vo.OrderVO;

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
}