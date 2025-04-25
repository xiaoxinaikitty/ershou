package com.xuchao.ershou.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 验证码验证历史实体类
 */
@Data
public class VerificationHistory {
    /**
     * 验证历史记录ID（唯一主键）
     */
    private Long historyId;
    
    /**
     * 进行验证码验证的手机号码
     */
    private String phoneNumber;
    
    /**
     * 用于验证的验证码
     */
    private String verificationCode;
    
    /**
     * 验证结果，false表示验证失败，true表示验证成功
     */
    private Boolean verificationResult;
    
    /**
     * 验证码验证时间
     */
    private LocalDateTime verificationTime;
    
    /**
     * 关联的验证码记录表中的id
     */
    private Long relatedRecordId;
} 