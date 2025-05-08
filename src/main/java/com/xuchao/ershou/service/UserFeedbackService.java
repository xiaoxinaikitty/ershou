package com.xuchao.ershou.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xuchao.ershou.model.dao.feedback.FeedbackAddDao;
import com.xuchao.ershou.model.dao.feedback.FeedbackReplyDao;
import com.xuchao.ershou.model.entity.UserFeedback;
import com.xuchao.ershou.model.vo.UserFeedbackVO;

/**
 * 用户反馈Service接口
 */
public interface UserFeedbackService extends IService<UserFeedback> {

    /**
     * 提交用户反馈
     * @param userId 用户ID
     * @param feedbackAddDao 反馈信息
     * @return 成功返回反馈ID
     */
    Long addFeedback(Long userId, FeedbackAddDao feedbackAddDao);

    /**
     * 分页查询当前用户的反馈列表
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<UserFeedbackVO> getUserFeedbackList(Long userId, Integer pageNum, Integer pageSize, Integer status);

    /**
     * 查询反馈详情
     * @param feedbackId 反馈ID
     * @return 反馈详情
     */
    UserFeedbackVO getFeedbackDetail(Long feedbackId);

    /**
     * 管理员回复反馈
     * @param adminId 管理员ID
     * @param replyDao 回复信息
     * @return 是否成功
     */
    boolean replyFeedback(Long adminId, FeedbackReplyDao replyDao);

    /**
     * 管理员分页查询反馈列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param status 状态（可选）
     * @param feedbackType 反馈类型（可选）
     * @return 分页结果
     */
    IPage<UserFeedbackVO> getFeedbackList(Integer pageNum, Integer pageSize, Integer status, Integer feedbackType);

    /**
     * 管理员设置反馈优先级
     * @param feedbackId 反馈ID
     * @param priorityLevel 优先级(0普通 1重要 2紧急)
     * @return 是否成功
     */
    boolean setPriorityLevel(Long feedbackId, Integer priorityLevel);

    /**
     * 删除反馈（用户只能删除自己的）
     * @param userId 用户ID
     * @param feedbackId 反馈ID
     * @return 是否成功
     */
    boolean deleteFeedback(Long userId, Long feedbackId);
} 