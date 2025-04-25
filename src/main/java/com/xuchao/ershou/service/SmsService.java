package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dto.SmsCodeDTO;
import com.xuchao.ershou.model.dto.SmsVerifyDTO;

/**
 * 短信服务接口
 */
public interface SmsService {
    /**
     * 发送验证码
     *
     * @param smsCodeDTO 短信验证码DTO
     * @return 发送结果
     */
    boolean sendVerificationCode(SmsCodeDTO smsCodeDTO);

    /**
     * 验证验证码
     *
     * @param smsVerifyDTO 短信验证DTO
     * @return 验证结果
     */
    boolean verifyCode(SmsVerifyDTO smsVerifyDTO);
    
    /**
     * 使验证码失效
     *
     * @param phoneNumber 手机号
     * @param businessType 业务类型
     */
    void invalidateCode(String phoneNumber, String businessType);
} 