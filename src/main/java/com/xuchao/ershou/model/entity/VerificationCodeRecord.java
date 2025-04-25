package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 验证码记录实体类
 */
@Data
@Accessors(chain = true)
@TableName("verification_code_record")
public class VerificationCodeRecord {
    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 手机号码
     */
    private String phoneNumber;
    
    /**
     * 验证码
     */
    private String verificationCode;
    
    /**
     * 业务类型
     */
    private String businessType;
    
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
    
    /**
     * 过期时间
     */
    private LocalDateTime expirationTime;
    
    /**
     * 是否已使用
     */
    private Boolean isUsed;
    
    /**
     * 尝试次数
     */
    private Integer attemptCount;
} 