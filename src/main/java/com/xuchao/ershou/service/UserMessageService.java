package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dto.UserMessageRequest;
import com.xuchao.ershou.model.vo.UserMessageVO;
import com.xuchao.ershou.model.vo.ConversationVO;

import java.util.List;

/**
 * 用户消息服务接口
 */
public interface UserMessageService {
    
    /**
     * 发送消息
     * @param request 消息请求
     * @param senderId 发送者ID
     * @return 消息信息
     */
    UserMessageVO sendMessage(UserMessageRequest request, Long senderId);
    
    /**
     * 获取会话的消息列表
     * @param conversationId 会话ID
     * @return 消息列表
     */
    List<UserMessageVO> getMessagesByConversationId(Long conversationId);
    
    /**
     * 根据商品ID和用户ID获取消息列表
     * @param productId 商品ID
     * @param currentUserId 当前用户ID
     * @return 消息列表
     */
    List<UserMessageVO> getMessagesByProductAndUser(Long productId, Long currentUserId);
    
    /**
     * 获取用户的会话列表
     * @param userId 用户ID
     * @return 会话列表
     */
    List<ConversationVO> getUserConversations(Long userId);
    
    /**
     * 获取卖家的会话列表
     * @param sellerId 卖家ID
     * @return 会话列表
     */
    List<ConversationVO> getSellerConversations(Long sellerId);
    
    /**
     * 标记消息为已读
     * @param messageId 消息ID
     * @return 是否成功
     */
    boolean markMessageAsRead(Long messageId);
    
    /**
     * 标记会话的所有消息为已读
     * @param conversationId 会话ID
     * @param currentUserId 当前用户ID
     * @return 是否成功
     */
    boolean markConversationAsRead(Long conversationId, Long currentUserId);
    
    /**
     * 关闭会话
     * @param conversationId 会话ID
     * @return 是否成功
     */
    boolean closeConversation(Long conversationId);
} 