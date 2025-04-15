package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.JwtUtil;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dto.OrderMessageRequest;
import com.xuchao.ershou.model.vo.OrderMessageVO;
import com.xuchao.ershou.service.OrderMessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单消息控制器
 */
@RestController
@RequestMapping("/order/message")
public class OrderMessageController {
    
    @Autowired
    private OrderMessageService orderMessageService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 发送订单消息（催促发货）
     * @param request 消息请求
     * @param httpServletRequest HTTP请求
     * @return 消息信息
     */
    @PostMapping("/send")
    public BaseResponse<OrderMessageVO> sendMessage(@RequestBody @Valid OrderMessageRequest request, 
                                                  HttpServletRequest httpServletRequest) {
        // 验证用户身份
        String token = httpServletRequest.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        
        // 验证当前用户
        Long currentUserId = jwtUtil.getUserIdFromToken(token.substring(7));
        if (!currentUserId.equals(request.getSenderId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 处理消息发送
        OrderMessageVO messageVO = orderMessageService.sendMessage(request);
        return ResultUtils.success(messageVO);
    }
    
    /**
     * 获取订单消息列表
     * @param orderId 订单ID
     * @return 消息列表
     */
    @GetMapping("/list/{orderId}")
    public BaseResponse<List<OrderMessageVO>> getMessageList(@PathVariable Long orderId) {
        List<OrderMessageVO> messageList = orderMessageService.getMessagesByOrderId(orderId);
        return ResultUtils.success(messageList);
    }
    
    /**
     * 获取接收的消息列表
     * @param userId 用户ID
     * @return 消息列表
     */
    @GetMapping("/received/{userId}")
    public BaseResponse<List<OrderMessageVO>> getReceivedMessages(@PathVariable Long userId) {
        List<OrderMessageVO> messageList = orderMessageService.getReceivedMessages(userId);
        return ResultUtils.success(messageList);
    }
    
    /**
     * 标记消息为已读
     * @param messageId 消息ID
     * @return 是否成功
     */
    @PutMapping("/read/{messageId}")
    public BaseResponse<Boolean> markAsRead(@PathVariable Long messageId) {
        boolean result = orderMessageService.markAsRead(messageId);
        return ResultUtils.success(result);
    }
} 