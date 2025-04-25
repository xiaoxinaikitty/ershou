package com.xuchao.ershou.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 短信发送工具类
 * 注意：这里只是模拟短信发送功能，实际项目中需要集成第三方短信服务商API
 */
@Slf4j
@Component
public class SmsUtils {

    /**
     * 发送短信验证码
     * @param phoneNumber 手机号
     * @param verificationCode 验证码
     * @return 是否发送成功
     */
    public boolean sendVerificationCode(String phoneNumber, String verificationCode) {
        // 实际项目中需要调用短信服务商的API发送短信
        // 这里仅做模拟，输出日志
        log.info("发送验证码短信到手机号: {}, 验证码: {}", phoneNumber, verificationCode);
        
        // 假设短信发送成功
        return true;
    }
} 