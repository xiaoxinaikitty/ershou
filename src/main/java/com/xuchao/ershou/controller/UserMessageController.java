package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dto.UserMessageRequest;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.model.vo.ConversationVO;
import com.xuchao.ershou.model.vo.UserMessageVO;
import com.xuchao.ershou.service.UserMessageService;
import com.xuchao.ershou.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 用户消息接口
 */
@RestController
@RequestMapping("/user/message")
@Slf4j
public class UserMessageController {

    @Autowired
    private UserMessageService userMessageService;
    
    @Autowired
    private UserService userService;

    /**
     * 发送消息
     * @param request 消息请求
     * @return 消息信息
     */
    @PostMapping("/send")
    public BaseResponse<UserMessageVO> sendMessage(@RequestBody @Valid UserMessageRequest request) {
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        UserMessageVO messageVO = userMessageService.sendMessage(request, currentUserId);
        return ResultUtils.success(messageVO);
    }

    /**
     * 根据商品ID和用户ID获取消息列表
     * @param productId 商品ID
     * @return 消息列表
     */
    @GetMapping("/list/product/{productId}")
    public BaseResponse<List<UserMessageVO>> getMessagesByProduct(@PathVariable Long productId) {
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        List<UserMessageVO> messages = userMessageService.getMessagesByProductAndUser(productId, currentUserId);
        return ResultUtils.success(messages);
    }

    /**
     * 根据会话ID获取消息列表
     * @param conversationId 会话ID
     * @return 消息列表
     */
    @GetMapping("/list/conversation/{conversationId}")
    public BaseResponse<List<UserMessageVO>> getMessagesByConversation(@PathVariable Long conversationId) {
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        List<UserMessageVO> messages = userMessageService.getMessagesByConversationId(conversationId);
        return ResultUtils.success(messages);
    }

    /**
     * 获取用户的会话列表
     * @return 会话列表
     */
    @GetMapping("/conversations/user")
    public BaseResponse<List<ConversationVO>> getUserConversations() {
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        List<ConversationVO> conversations = userMessageService.getUserConversations(currentUserId);
        return ResultUtils.success(conversations);
    }

    /**
     * 获取卖家的会话列表
     * @return 会话列表
     */
    @GetMapping("/conversations/seller")
    public BaseResponse<List<ConversationVO>> getSellerConversations() {
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        List<ConversationVO> conversations = userMessageService.getSellerConversations(currentUserId);
        return ResultUtils.success(conversations);
    }

    /**
     * 标记消息为已读
     * @param messageId 消息ID
     * @return 是否成功
     */
    @PostMapping("/read/{messageId}")
    public BaseResponse<Boolean> markMessageAsRead(@PathVariable Long messageId) {
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        boolean result = userMessageService.markMessageAsRead(messageId);
        return ResultUtils.success(result);
    }

    /**
     * 标记会话的所有消息为已读
     * @param conversationId 会话ID
     * @return 是否成功
     */
    @PostMapping("/read/conversation/{conversationId}")
    public BaseResponse<Boolean> markConversationAsRead(@PathVariable Long conversationId) {
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        boolean result = userMessageService.markConversationAsRead(conversationId, currentUserId);
        return ResultUtils.success(result);
    }

    /**
     * 关闭会话
     * @param conversationId 会话ID
     * @return 是否成功
     */
    @PostMapping("/close/{conversationId}")
    public BaseResponse<Boolean> closeConversation(@PathVariable Long conversationId) {
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        boolean result = userMessageService.closeConversation(conversationId);
        return ResultUtils.success(result);
    }
} 