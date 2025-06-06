package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dao.order.OrderCancelDao;
import com.xuchao.ershou.model.dao.order.OrderCreateDao;
import com.xuchao.ershou.model.dto.OrderConfirmReceiptRequest;
import com.xuchao.ershou.model.dto.OrderNotifyShipmentRequest;
import com.xuchao.ershou.model.dto.OrderPayRequest;
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
    
    /**
     * 取消订单
     * @param userId 当前登录用户ID（买家）
     * @param orderCancelDao 订单取消信息
     * @return 取消后的订单详情
     */
    OrderVO cancelOrder(Long userId, OrderCancelDao orderCancelDao);

    /**
     * 支付订单
     * @param request 支付请求
     * @return 订单信息
     */
    OrderVO payOrder(OrderPayRequest request);
    
    /**
     * 确认收货
     * @param request 确认收货请求
     * @return 更新后的订单信息
     */
    OrderVO confirmReceipt(OrderConfirmReceiptRequest request);
    
    /**
     * 通知收货（卖家发货）
     * @param request 通知收货请求
     * @return 更新后的订单信息
     */
    OrderVO notifyShipment(OrderNotifyShipmentRequest request);

    /**
     * 根据用户ID和订单状态统计订单数量
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单数量
     */
    int countOrdersByStatus(Long userId, Integer status);
}