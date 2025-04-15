package com.xuchao.ershou.mapper;

import com.xuchao.ershou.model.entity.OrderMessage;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 订单消息Mapper接口
 */
@Mapper
public interface OrderMessageMapper {
    /**
     * 插入订单消息
     * @param orderMessage 消息信息
     * @return 影响行数
     */
    int insertOrderMessage(OrderMessage orderMessage);
    
    /**
     * 根据订单ID查询消息列表
     * @param orderId 订单ID
     * @return 消息列表
     */
    List<OrderMessage> selectByOrderId(Long orderId);
    
    /**
     * 根据用户ID查询收到的消息
     * @param receiverId 接收者ID
     * @return 消息列表
     */
    List<OrderMessage> selectByReceiverId(Long receiverId);
    
    /**
     * 根据用户ID查询发送的消息
     * @param senderId 发送者ID
     * @return 消息列表
     */
    List<OrderMessage> selectBySenderId(Long senderId);
    
    /**
     * 更新消息已读状态
     * @param messageId 消息ID
     * @return 影响行数
     */
    int updateMessageReadStatus(Long messageId);
} 