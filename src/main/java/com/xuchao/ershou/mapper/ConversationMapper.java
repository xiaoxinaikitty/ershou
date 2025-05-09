package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会话Mapper接口
 */
@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {

    /**
     * 根据用户ID查询会话列表
     * @param userId 用户ID
     * @return 会话列表
     */
    List<Conversation> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据卖家ID查询会话列表
     * @param sellerId 卖家ID
     * @return 会话列表
     */
    List<Conversation> selectBySellerId(@Param("sellerId") Long sellerId);

    /**
     * 根据商品ID和用户ID查询会话
     * @param productId 商品ID
     * @param userId 用户ID
     * @return 会话
     */
    Conversation selectByProductAndUser(@Param("productId") Long productId, @Param("userId") Long userId);

    /**
     * 更新会话的最后消息
     * @param conversationId 会话ID
     * @param lastMessageContent 最后消息内容
     * @return 影响行数
     */
    int updateLastMessage(@Param("conversationId") Long conversationId, 
                         @Param("lastMessageContent") String lastMessageContent);

    /**
     * 增加用户未读消息数
     * @param conversationId 会话ID
     * @return 影响行数
     */
    int increaseUserUnreadCount(@Param("conversationId") Long conversationId);

    /**
     * 增加卖家未读消息数
     * @param conversationId 会话ID
     * @return 影响行数
     */
    int increaseSellerUnreadCount(@Param("conversationId") Long conversationId);

    /**
     * 重置用户未读消息数
     * @param conversationId 会话ID
     * @return 影响行数
     */
    int resetUserUnreadCount(@Param("conversationId") Long conversationId);

    /**
     * 重置卖家未读消息数
     * @param conversationId 会话ID
     * @return 影响行数
     */
    int resetSellerUnreadCount(@Param("conversationId") Long conversationId);

    /**
     * 更新会话状态
     * @param conversationId 会话ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("conversationId") Long conversationId, @Param("status") Integer status);
} 