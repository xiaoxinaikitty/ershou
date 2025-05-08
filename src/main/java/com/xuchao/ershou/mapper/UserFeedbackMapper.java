package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuchao.ershou.model.entity.UserFeedback;
import com.xuchao.ershou.model.vo.UserFeedbackVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户反馈Mapper接口
 */
@Mapper
public interface UserFeedbackMapper extends BaseMapper<UserFeedback> {

    /**
     * 分页查询用户反馈列表
     * @param page 分页参数
     * @param status 状态（可选）
     * @param feedbackType 反馈类型（可选）
     * @return 分页结果
     */
    IPage<UserFeedbackVO> selectFeedbackList(
            Page<UserFeedbackVO> page,
            @Param("status") Integer status,
            @Param("feedbackType") Integer feedbackType);

    /**
     * 分页查询当前用户的反馈列表
     * @param page 分页参数
     * @param userId 用户ID
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<UserFeedbackVO> selectUserFeedbackList(
            Page<UserFeedbackVO> page,
            @Param("userId") Long userId,
            @Param("status") Integer status);

    /**
     * 查询反馈详情
     * @param feedbackId 反馈ID
     * @return 反馈详情
     */
    UserFeedbackVO selectFeedbackDetail(@Param("feedbackId") Long feedbackId);
} 