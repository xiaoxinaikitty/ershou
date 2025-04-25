package com.xuchao.ershou.mapper;

import com.xuchao.ershou.model.entity.VerificationCodeRecord;
import com.xuchao.ershou.model.entity.VerificationHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 验证码相关数据库操作接口
 */
@Mapper
public interface VerificationCodeMapper {
    
    /**
     * 插入新的验证码记录
     * @param record 验证码记录
     * @return 影响的行数
     */
    int insertVerificationCode(VerificationCodeRecord record);
    
    /**
     * 根据手机号获取最新的未使用验证码
     * @param phoneNumber 手机号
     * @return 验证码记录
     */
    VerificationCodeRecord getLatestValidCode(@Param("phoneNumber") String phoneNumber);
    
    /**
     * 更新验证码状态为已使用
     * @param id 验证码记录ID
     * @return 影响的行数
     */
    int updateCodeUsedStatus(@Param("id") Long id);
    
    /**
     * 增加验证尝试次数
     * @param id 验证码记录ID
     * @return 影响的行数
     */
    int incrementAttemptCount(@Param("id") Long id);
    
    /**
     * 插入验证历史记录
     * @param history 验证历史记录
     * @return 影响的行数
     */
    int insertVerificationHistory(VerificationHistory history);
} 