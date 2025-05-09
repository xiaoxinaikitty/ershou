package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.mapper.ConversationMapper;
import com.xuchao.ershou.mapper.UserMessageMapper;
import com.xuchao.ershou.mapper.ProductMapper;
import com.xuchao.ershou.mapper.UserMapper;
import com.xuchao.ershou.model.dto.UserMessageRequest;
import com.xuchao.ershou.model.entity.Conversation;
import com.xuchao.ershou.model.entity.Product;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.model.entity.UserMessage;
import com.xuchao.ershou.model.vo.ConversationVO;
import com.xuchao.ershou.model.vo.UserMessageVO;
import com.xuchao.ershou.service.UserMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户消息服务实现类
 */
@Service
@Slf4j
public class UserMessageServiceImpl implements UserMessageService {

    @Autowired
    private UserMessageMapper userMessageMapper;

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserMessageVO sendMessage(UserMessageRequest request, Long senderId) {
        // 参数校验
        if (request == null || senderId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long productId = request.getProductId();
        Long receiverId = request.getReceiverId();
        String content = request.getContent();

        if (productId == null || receiverId == null || content == null || content.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息内容或接收者不能为空");
        }

        // 校验商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商品不存在");
        }

        // 校验发送者和接收者是否存在
        User sender = userMapper.selectById(senderId);
        User receiver = userMapper.selectById(receiverId);
        if (sender == null || receiver == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 判断消息是由买家发给卖家，还是卖家发给买家
        boolean isBuyerToSeller = !senderId.equals(product.getUserId());
        Long userId = isBuyerToSeller ? senderId : receiverId;
        Long sellerId = isBuyerToSeller ? receiverId : senderId;

        // 查询或创建会话
        Conversation conversation = conversationMapper.selectByProductAndUser(productId, userId);
        if (conversation == null) {
            // 创建新会话
            LocalDateTime now = LocalDateTime.now();
            conversation = new Conversation()
                    .setProductId(productId)
                    .setUserId(userId)
                    .setSellerId(sellerId)
                    .setLastMessageContent(content)
                    .setLastMessageTime(now)
                    .setUserUnreadCount(isBuyerToSeller ? 0 : 1)
                    .setSellerUnreadCount(isBuyerToSeller ? 1 : 0)
                    .setStatus(1);
            conversationMapper.insert(conversation);
        } else {
            // 更新会话
            if (isBuyerToSeller) {
                conversationMapper.increaseSellerUnreadCount(conversation.getConversationId());
            } else {
                conversationMapper.increaseUserUnreadCount(conversation.getConversationId());
            }
            conversationMapper.updateLastMessage(conversation.getConversationId(), content);
        }

        // 创建消息
        UserMessage userMessage = new UserMessage()
                .setProductId(productId)
                .setSenderId(senderId)
                .setReceiverId(receiverId)
                .setContent(content)
                .setImageUrl(request.getImageUrl())
                .setIsRead(0);

        userMessageMapper.insert(userMessage);

        // 返回消息视图对象
        return buildUserMessageVO(userMessage, product, sender, receiver);
    }

    @Override
    public List<UserMessageVO> getMessagesByConversationId(Long conversationId) {
        if (conversationId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "会话不存在");
        }

        List<UserMessage> messages = userMessageMapper.selectByConversationId(conversationId);
        return messages.stream()
                .map(this::convertToUserMessageVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserMessageVO> getMessagesByProductAndUser(Long productId, Long currentUserId) {
        if (productId == null || currentUserId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 校验商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商品不存在");
        }

        // 确定用户和卖家的身份
        Long sellerId = product.getUserId();
        Long userId = currentUserId;

        // 如果当前用户是卖家，则调整身份
        if (currentUserId.equals(sellerId)) {
            Conversation conversation = conversationMapper.selectByProductAndUser(productId, userId);
            if (conversation != null) {
                userId = conversation.getUserId();
            } else {
                // 如果没有会话，说明没有消息
                return new ArrayList<>();
            }
        }

        List<UserMessage> messages = userMessageMapper.selectByProductAndUser(productId, userId, sellerId);
        return messages.stream()
                .map(this::convertToUserMessageVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConversationVO> getUserConversations(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        List<Conversation> conversations = conversationMapper.selectByUserId(userId);
        return conversations.stream()
                .map(this::convertToConversationVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConversationVO> getSellerConversations(Long sellerId) {
        if (sellerId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        List<Conversation> conversations = conversationMapper.selectBySellerId(sellerId);
        return conversations.stream()
                .map(this::convertToConversationVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markMessageAsRead(Long messageId) {
        if (messageId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        int result = userMessageMapper.updateMessageReadStatus(messageId);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markConversationAsRead(Long conversationId, Long currentUserId) {
        if (conversationId == null || currentUserId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "会话不存在");
        }

        // 判断当前用户是买家还是卖家
        boolean isUser = currentUserId.equals(conversation.getUserId());
        boolean isSeller = currentUserId.equals(conversation.getSellerId());

        if (!isUser && !isSeller) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作该会话");
        }

        // 重置对应的未读消息计数
        if (isUser) {
            conversationMapper.resetUserUnreadCount(conversationId);
        } else {
            conversationMapper.resetSellerUnreadCount(conversationId);
        }

        // 标记接收的消息为已读
        return userMessageMapper.markConversationAsRead(
                conversation.getProductId(),
                conversation.getUserId(),
                conversation.getSellerId(),
                currentUserId) > 0;
    }

    @Override
    public boolean closeConversation(Long conversationId) {
        if (conversationId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "会话不存在");
        }

        int result = conversationMapper.updateStatus(conversationId, 0);
        return result > 0;
    }

    /**
     * 构建用户消息视图对象
     */
    private UserMessageVO buildUserMessageVO(UserMessage message, Product product, User sender, User receiver) {
        UserMessageVO vo = new UserMessageVO();
        vo.setMessageId(message.getMessageId());
        vo.setProductId(message.getProductId());
        vo.setProductTitle(product.getTitle());
        // 设置商品主图（此处实际应该获取商品的主图）
        
        vo.setSenderId(message.getSenderId());
        vo.setSenderUsername(sender.getUsername());
        vo.setSenderAvatar(sender.getAvatar());
        
        vo.setReceiverId(message.getReceiverId());
        vo.setReceiverUsername(receiver.getUsername());
        vo.setReceiverAvatar(receiver.getAvatar());
        
        vo.setContent(message.getContent());
        vo.setImageUrl(message.getImageUrl());
        vo.setIsRead(message.getIsRead());
        vo.setCreatedTime(message.getCreatedTime());
        return vo;
    }

    /**
     * 将消息实体转换为VO
     */
    private UserMessageVO convertToUserMessageVO(UserMessage message) {
        if (message == null) {
            return null;
        }

        // 查询相关信息
        Product product = productMapper.selectById(message.getProductId());
        User sender = userMapper.selectById(message.getSenderId());
        User receiver = userMapper.selectById(message.getReceiverId());

        if (product == null || sender == null || receiver == null) {
            log.error("消息关联的商品或用户不存在: messageId={}", message.getMessageId());
            return null;
        }

        return buildUserMessageVO(message, product, sender, receiver);
    }

    /**
     * 将会话实体转换为VO
     */
    private ConversationVO convertToConversationVO(Conversation conversation) {
        if (conversation == null) {
            return null;
        }

        ConversationVO vo = new ConversationVO();
        vo.setConversationId(conversation.getConversationId());
        vo.setProductId(conversation.getProductId());
        
        // 获取商品信息
        Product product = productMapper.selectById(conversation.getProductId());
        if (product != null) {
            vo.setProductTitle(product.getTitle());
            // 设置商品主图（此处实际应该获取商品的主图）
        }
        
        // 设置用户信息
        User user = userMapper.selectById(conversation.getUserId());
        if (user != null) {
            vo.setUserId(user.getUserId());
            vo.setUsername(user.getUsername());
            vo.setUserAvatar(user.getAvatar());
        }
        
        // 设置卖家信息
        User seller = userMapper.selectById(conversation.getSellerId());
        if (seller != null) {
            vo.setSellerId(seller.getUserId());
            vo.setSellerUsername(seller.getUsername());
            vo.setSellerAvatar(seller.getAvatar());
        }
        
        vo.setLastMessageContent(conversation.getLastMessageContent());
        vo.setLastMessageTime(conversation.getLastMessageTime());
        // 设置未读数（根据用户角色决定显示哪个未读数）
        vo.setUnreadCount(conversation.getUserUnreadCount()); // 默认显示用户未读数
        vo.setStatus(conversation.getStatus());
        vo.setCreatedTime(conversation.getCreatedTime());
        
        return vo;
    }
} 