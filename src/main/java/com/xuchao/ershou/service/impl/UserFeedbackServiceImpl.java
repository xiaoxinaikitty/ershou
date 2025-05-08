package com.xuchao.ershou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuchao.ershou.common.enums.FeedbackStatusEnum;
import com.xuchao.ershou.model.dao.feedback.FeedbackAddDao;
import com.xuchao.ershou.model.dao.feedback.FeedbackReplyDao;
import com.xuchao.ershou.model.entity.UserFeedback;
import com.xuchao.ershou.model.vo.UserFeedbackVO;
import com.xuchao.ershou.mapper.UserFeedbackMapper;
import com.xuchao.ershou.service.UserFeedbackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户反馈Service实现类
 */
@Service
public class UserFeedbackServiceImpl extends ServiceImpl<UserFeedbackMapper, UserFeedback> implements UserFeedbackService {

    @Override
    @Transactional
    public Long addFeedback(Long userId, FeedbackAddDao feedbackAddDao) {
        UserFeedback feedback = new UserFeedback();
        feedback.setUserId(userId);
        feedback.setFeedbackType(feedbackAddDao.getFeedbackType());
        feedback.setFeedbackTitle(feedbackAddDao.getFeedbackTitle());
        feedback.setFeedbackContent(feedbackAddDao.getFeedbackContent());
        feedback.setContactInfo(feedbackAddDao.getContactInfo());
        feedback.setImages(feedbackAddDao.getImages());
        feedback.setStatus(FeedbackStatusEnum.UNPROCESSED.getCode());
        feedback.setPriorityLevel(0); // 默认普通优先级
        feedback.setCreatedTime(LocalDateTime.now());
        
        save(feedback);
        return feedback.getFeedbackId();
    }

    @Override
    public IPage<UserFeedbackVO> getUserFeedbackList(Long userId, Integer pageNum, Integer pageSize, Integer status) {
        Page<UserFeedbackVO> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectUserFeedbackList(page, userId, status);
    }

    @Override
    public UserFeedbackVO getFeedbackDetail(Long feedbackId) {
        return baseMapper.selectFeedbackDetail(feedbackId);
    }

    @Override
    @Transactional
    public boolean replyFeedback(Long adminId, FeedbackReplyDao replyDao) {
        UserFeedback feedback = getById(replyDao.getFeedbackId());
        if (feedback == null) {
            return false;
        }
        
        feedback.setAdminId(adminId);
        feedback.setAdminReply(replyDao.getAdminReply());
        feedback.setStatus(replyDao.getStatus());
        feedback.setReplyTime(LocalDateTime.now());
        
        return updateById(feedback);
    }

    @Override
    public IPage<UserFeedbackVO> getFeedbackList(Integer pageNum, Integer pageSize, Integer status, Integer feedbackType) {
        Page<UserFeedbackVO> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectFeedbackList(page, status, feedbackType);
    }

    @Override
    public boolean setPriorityLevel(Long feedbackId, Integer priorityLevel) {
        UserFeedback feedback = getById(feedbackId);
        if (feedback == null) {
            return false;
        }
        
        feedback.setPriorityLevel(priorityLevel);
        return updateById(feedback);
    }

    @Override
    public boolean deleteFeedback(Long userId, Long feedbackId) {
        LambdaQueryWrapper<UserFeedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFeedback::getFeedbackId, feedbackId);
        wrapper.eq(UserFeedback::getUserId, userId);
        
        return remove(wrapper);
    }
} 