package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.UserMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户消息Mapper接口
 */
@Mapper
public interface UserMessageMapper extends BaseMapper<UserMessage> {

    /**
     * 根据会话ID查询消息列表
     * @param conversationId 会话ID
     * @return 消息列表
     */
    List<UserMessage> selectByConversationId(@Param("conversationId") Long conversationId);

    /**
     * 根据商品ID和用户ID查询消息列表
     * @param productId 商品ID
     * @param userId 用户ID
     * @param sellerId 卖家ID
     * @return 消息列表
     */
    List<UserMessage> selectByProductAndUser(@Param("productId") Long productId, 
                                            @Param("userId") Long userId,
                                            @Param("sellerId") Long sellerId);

    /**
     * 根据用户ID查询接收到的消息
     * @param receiverId 接收者ID
     * @return 消息列表
     */
    List<UserMessage> selectByReceiverId(@Param("receiverId") Long receiverId);

    /**
     * 根据用户ID查询发送的消息
     * @param senderId 发送者ID
     * @return 消息列表
     */
    List<UserMessage> selectBySenderId(@Param("senderId") Long senderId);

    /**
     * 标记消息为已读
     * @param messageId 消息ID
     * @return 影响行数
     */
    int updateMessageReadStatus(@Param("messageId") Long messageId);

    /**
     * 批量标记消息为已读
     * @param messageIds 消息ID列表
     * @return 影响行数
     */
    int batchUpdateMessageReadStatus(@Param("messageIds") List<Long> messageIds);

    /**
     * 标记会话中的所有消息为已读
     * @param productId 商品ID
     * @param userId 用户ID
     * @param sellerId 卖家ID
     * @param receiverId 接收者ID
     * @return 影响行数
     */
    int markConversationAsRead(@Param("productId") Long productId,
                               @Param("userId") Long userId,
                               @Param("sellerId") Long sellerId,
                               @Param("receiverId") Long receiverId);
} 