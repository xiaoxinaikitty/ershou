package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dto.OrderMessageRequest;
import com.xuchao.ershou.model.vo.OrderMessageVO;
import java.util.List;

/**
 * 订单消息服务接口
 */
public interface OrderMessageService {
    
    /**
     * 发送订单消息
     * @param request 消息请求
     * @return 消息信息
     */
    OrderMessageVO sendMessage(OrderMessageRequest request);
    
    /**
     * 根据订单ID获取消息列表
     * @param orderId 订单ID
     * @return 消息列表
     */
    List<OrderMessageVO> getMessagesByOrderId(Long orderId);
    
    /**
     * 根据用户ID获取收到的消息
     * @param receiverId 接收者ID
     * @return 消息列表
     */
    List<OrderMessageVO> getReceivedMessages(Long receiverId);
    
    /**
     * 标记消息为已读
     * @param messageId 消息ID
     * @return 是否成功
     */
    boolean markAsRead(Long messageId);
} 