package com.xuchao.ershou.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xuchao.ershou.common.ApiResponse;
import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dao.feedback.FeedbackAddDao;
import com.xuchao.ershou.model.dao.feedback.FeedbackReplyDao;
import com.xuchao.ershou.model.vo.UserFeedbackVO;
import com.xuchao.ershou.service.UserFeedbackService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户反馈Controller
 */
@RestController
@Validated
public class UserFeedbackController {

    @Autowired
    private UserFeedbackService userFeedbackService;

    /**
     * 提交用户反馈
     * @param feedbackAddDao 反馈信息
     * @return 反馈ID
     */
    @PostMapping("/feedback/submit")
    public BaseResponse<Long> submitFeedback(@RequestBody @Valid FeedbackAddDao feedbackAddDao) {
        // 获取当前用户ID
        Long userId = CurrentUserUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        Long feedbackId = userFeedbackService.addFeedback(userId, feedbackAddDao);
        return ResultUtils.success(feedbackId);
    }

    /**
     * 分页查询当前用户的反馈列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param status 状态（可选）
     * @return 分页结果
     */
    @GetMapping("/feedback/my-list")
    public BaseResponse<Map<String, Object>> getMyFeedbackList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") @Max(50) Integer pageSize,
            @RequestParam(required = false) Integer status) {
        
        // 获取当前用户ID
        Long userId = CurrentUserUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        IPage<UserFeedbackVO> page = userFeedbackService.getUserFeedbackList(userId, pageNum, pageSize, status);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", page.getRecords());
        result.put("total", page.getTotal());
        result.put("pageNum", page.getCurrent());
        result.put("pageSize", page.getSize());
        result.put("pages", page.getPages());
        result.put("hasNext", page.getCurrent() < page.getPages());
        result.put("hasPrevious", page.getCurrent() > 1);
        
        return ResultUtils.success(result);
    }

    /**
     * 查询反馈详情
     * @param feedbackId 反馈ID
     * @return 反馈详情
     */
    @GetMapping("/feedback/detail/{feedbackId}")
    public BaseResponse<UserFeedbackVO> getFeedbackDetail(@PathVariable Long feedbackId) {
        // 获取当前用户ID
        Long userId = CurrentUserUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        UserFeedbackVO feedbackDetail = userFeedbackService.getFeedbackDetail(feedbackId);
        if (feedbackDetail == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "反馈不存在");
        }
        
        // 普通用户只能查看自己的反馈详情
        if (!CurrentUserUtils.isAdmin(userId) && !feedbackDetail.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权查看其他用户的反馈");
        }
        
        return ResultUtils.success(feedbackDetail);
    }

    /**
     * 删除反馈
     * @param feedbackId 反馈ID
     * @return 是否成功
     */
    @DeleteMapping("/feedback/delete/{feedbackId}")
    public BaseResponse<Boolean> deleteFeedback(@PathVariable Long feedbackId) {
        // 获取当前用户ID
        Long userId = CurrentUserUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        boolean success = userFeedbackService.deleteFeedback(userId, feedbackId);
        return ResultUtils.success(success);
    }

    /**
     * 管理员回复反馈
     * @param replyDao 回复信息
     * @return 是否成功
     */
    @PostMapping("/admin/feedback/reply")
    public BaseResponse<Boolean> replyFeedback(@RequestBody @Valid FeedbackReplyDao replyDao) {
        // 获取当前管理员ID
        Long adminId = CurrentUserUtils.getCurrentUserId();
        if (adminId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "管理员未登录");
        }
        
        // 验证是否为管理员
        if (!CurrentUserUtils.isAdmin(adminId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限回复反馈");
        }
        
        boolean success = userFeedbackService.replyFeedback(adminId, replyDao);
        return ResultUtils.success(success);
    }

    /**
     * 管理员分页查询反馈列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param status 状态（可选）
     * @param feedbackType 反馈类型（可选）
     * @return 分页结果
     */
    @GetMapping("/admin/feedback/list")
    public BaseResponse<Map<String, Object>> getFeedbackList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") @Max(50) Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer feedbackType) {
        
        // 获取当前用户ID
        Long userId = CurrentUserUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "管理员未登录");
        }
        
        // 验证是否为管理员
        if (!CurrentUserUtils.isAdmin(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限查看所有反馈");
        }
        
        IPage<UserFeedbackVO> page = userFeedbackService.getFeedbackList(pageNum, pageSize, status, feedbackType);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", page.getRecords());
        result.put("total", page.getTotal());
        result.put("pageNum", page.getCurrent());
        result.put("pageSize", page.getSize());
        result.put("pages", page.getPages());
        result.put("hasNext", page.getCurrent() < page.getPages());
        result.put("hasPrevious", page.getCurrent() > 1);
        
        return ResultUtils.success(result);
    }

    /**
     * 管理员设置反馈优先级
     * @param feedbackId 反馈ID
     * @param priorityLevel 优先级(0普通 1重要 2紧急)
     * @return 是否成功
     */
    @PutMapping("/admin/feedback/priority")
    public BaseResponse<Boolean> setPriorityLevel(
            @RequestParam Long feedbackId,
            @RequestParam @Min(0) @Max(2) Integer priorityLevel) {
        
        // 获取当前用户ID
        Long userId = CurrentUserUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "管理员未登录");
        }
        
        // 验证是否为管理员
        if (!CurrentUserUtils.isAdmin(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限设置优先级");
        }
        
        boolean success = userFeedbackService.setPriorityLevel(feedbackId, priorityLevel);
        return ResultUtils.success(success);
    }
    
    /**
     * 获取所有用户反馈消息的数量统计
     * @return 反馈数量统计
     */
    @GetMapping("/admin/feedback/count")
    public BaseResponse<Map<String, Object>> getFeedbackCount() {
        // 获取当前用户ID
        Long userId = CurrentUserUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "管理员未登录");
        }
        
        // 验证是否为管理员
        if (!CurrentUserUtils.isAdmin(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限查看反馈统计");
        }
        
        Map<String, Object> countData = userFeedbackService.getFeedbackCount();
        return ResultUtils.success(countData);
    }
} 