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
import java.util.HashMap;
import java.util.Map;

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
    
    @Override
    public Map<String, Object> getFeedbackCount() {
        // 创建结果Map
        Map<String, Object> resultMap = new HashMap<>();
        
        // 查询总数量
        long totalCount = count();
        resultMap.put("totalCount", totalCount);
        
        // 查询未处理数量
        LambdaQueryWrapper<UserFeedback> unprocessedQuery = new LambdaQueryWrapper<>();
        unprocessedQuery.eq(UserFeedback::getStatus, FeedbackStatusEnum.UNPROCESSED.getCode());
        long unprocessedCount = count(unprocessedQuery);
        resultMap.put("unprocessedCount", unprocessedCount);
        
        // 查询处理中数量
        LambdaQueryWrapper<UserFeedback> processingQuery = new LambdaQueryWrapper<>();
        processingQuery.eq(UserFeedback::getStatus, FeedbackStatusEnum.PROCESSING.getCode());
        long processingCount = count(processingQuery);
        resultMap.put("processingCount", processingCount);
        
        // 查询已处理数量
        LambdaQueryWrapper<UserFeedback> processedQuery = new LambdaQueryWrapper<>();
        processedQuery.eq(UserFeedback::getStatus, FeedbackStatusEnum.PROCESSED.getCode());
        long processedCount = count(processedQuery);
        resultMap.put("processedCount", processedCount);
        
        // 按反馈类型统计
        Map<String, Object> typeCounts = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            LambdaQueryWrapper<UserFeedback> typeQuery = new LambdaQueryWrapper<>();
            typeQuery.eq(UserFeedback::getFeedbackType, i);
            long typeCount = count(typeQuery);
            typeCounts.put("type" + i + "Count", typeCount);
        }
        resultMap.put("typeCounts", typeCounts);
        
        // 按优先级统计
        Map<String, Object> priorityCounts = new HashMap<>();
        
        // 普通优先级
        LambdaQueryWrapper<UserFeedback> normalQuery = new LambdaQueryWrapper<>();
        normalQuery.eq(UserFeedback::getPriorityLevel, 0);
        long normalCount = count(normalQuery);
        priorityCounts.put("normalCount", normalCount);
        
        // 重要优先级
        LambdaQueryWrapper<UserFeedback> importantQuery = new LambdaQueryWrapper<>();
        importantQuery.eq(UserFeedback::getPriorityLevel, 1);
        long importantCount = count(importantQuery);
        priorityCounts.put("importantCount", importantCount);
        
        // 紧急优先级
        LambdaQueryWrapper<UserFeedback> urgentQuery = new LambdaQueryWrapper<>();
        urgentQuery.eq(UserFeedback::getPriorityLevel, 2);
        long urgentCount = count(urgentQuery);
        priorityCounts.put("urgentCount", urgentCount);
        
        resultMap.put("priorityCounts", priorityCounts);
        
        return resultMap;
    }
} 