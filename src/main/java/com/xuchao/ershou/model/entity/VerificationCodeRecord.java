package com.xuchao.ershou.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 验证码记录实体类
 */
@Data
public class VerificationCodeRecord {
    /**
     * 记录ID（唯一主键）
     */
    private Long id;
    
    /**
     * 接收验证码的手机号码
     */
    private String phoneNumber;
    
    /**
     * 发送的验证码，一般为6位数字
     */
    private String verificationCode;
    
    /**
     * 验证码发送时间
     */
    private LocalDateTime sendTime;
    
    /**
     * 验证码过期时间
     */
    private LocalDateTime expirationTime;
    
    /**
     * 验证码是否已使用，0表示未使用，1表示已使用
     */
    private Boolean isUsed;
    
    /**
     * 验证码验证尝试次数
     */
    private Integer attemptCount;
} 