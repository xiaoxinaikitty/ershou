package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.mapper.OrderMapper;
import com.xuchao.ershou.mapper.OrderMessageMapper;
import com.xuchao.ershou.mapper.ProductMapper;
import com.xuchao.ershou.mapper.UserMapper;
import com.xuchao.ershou.model.dto.OrderMessageRequest;
import com.xuchao.ershou.model.entity.Order;
import com.xuchao.ershou.model.entity.OrderMessage;
import com.xuchao.ershou.model.entity.Product;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.model.vo.OrderMessageVO;
import com.xuchao.ershou.service.OrderMessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单消息服务实现类
 */
@Service
public class OrderMessageServiceImpl implements OrderMessageService {

    @Autowired
    private OrderMessageMapper orderMessageMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderMessageVO sendMessage(OrderMessageRequest request) {
        // 1. 参数校验
        if (request == null || request.getOrderId() == null || request.getProductId() == null 
                || request.getSenderId() == null || request.getReceiverId() == null
                || request.getContent() == null || request.getContent().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 2. 获取订单信息并验证
        Order order = orderMapper.selectOrderById(request.getOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }
        
        // 3. 验证订单与商品是否匹配
        if (!order.getProductId().equals(request.getProductId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单与商品不匹配");
        }
        
        // 4. 验证用户权限（发送者应该是订单的买家，接收者应该是订单的卖家）
        if (!order.getUserId().equals(request.getSenderId()) || !order.getSellerId().equals(request.getReceiverId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权发送此消息");
        }
        
        // 5. 创建消息对象
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setOrderId(request.getOrderId());
        orderMessage.setSenderId(request.getSenderId());
        orderMessage.setReceiverId(request.getReceiverId());
        orderMessage.setContent(request.getContent());
        orderMessage.setIsRead(0); // 初始未读
        orderMessage.setCreatedTime(LocalDateTime.now());
        
        // 6. 保存消息
        int result = orderMessageMapper.insertOrderMessage(orderMessage);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息发送失败");
        }
        
        // 7. 返回消息信息
        return convertToVO(orderMessage);
    }
    
    @Override
    public List<OrderMessageVO> getMessagesByOrderId(Long orderId) {
        if (orderId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        List<OrderMessage> messages = orderMessageMapper.selectByOrderId(orderId);
        if (messages == null || messages.isEmpty()) {
            return new ArrayList<>();
        }
        
        return messages.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    @Override
    public List<OrderMessageVO> getReceivedMessages(Long receiverId) {
        if (receiverId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        List<OrderMessage> messages = orderMessageMapper.selectByReceiverId(receiverId);
        if (messages == null || messages.isEmpty()) {
            return new ArrayList<>();
        }
        
        return messages.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRead(Long messageId) {
        if (messageId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        int result = orderMessageMapper.updateMessageReadStatus(messageId);
        return result > 0;
    }
    
    /**
     * 将消息实体转换为VO
     */
    private OrderMessageVO convertToVO(OrderMessage message) {
        OrderMessageVO vo = new OrderMessageVO();
        BeanUtils.copyProperties(message, vo);
        
        try {
            // 查询订单信息
            Order order = orderMapper.selectOrderById(message.getOrderId());
            if (order != null) {
                vo.setOrderNo(order.getOrderNo());
                vo.setProductId(order.getProductId());
                
                // 查询商品信息
                Product product = productMapper.selectProductById(order.getProductId());
                if (product != null) {
                    vo.setProductTitle(product.getTitle());
                }
            }
            
            // 查询发送者信息
            User sender = userMapper.selectById(message.getSenderId());
            if (sender != null) {
                vo.setSenderName(sender.getUsername());
            }
            
            // 查询接收者信息
            User receiver = userMapper.selectById(message.getReceiverId());
            if (receiver != null) {
                vo.setReceiverName(receiver.getUsername());
            }
        } catch (Exception e) {
            // 忽略异常，继续处理
        }
        
        return vo;
    }
} 