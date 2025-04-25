package com.xuchao.ershou.common.constant;

/**
 * Redis常量类
 */
public class RedisConstants {
    /**
     * 验证码前缀
     */
    public static final String SMS_CODE_PREFIX = "sms:code:";
    
    /**
     * 验证码过期时间（分钟）
     */
    public static final long SMS_CODE_EXPIRE_MINUTES = 5;
    
    /**
     * 验证码每日发送次数前缀
     */
    public static final String SMS_CODE_DAILY_COUNT_PREFIX = "sms:daily:count:";
    
    /**
     * 验证码每日发送次数过期时间（秒）- 24小时
     */
    public static final long SMS_CODE_DAILY_COUNT_EXPIRE_SECONDS = 24 * 60 * 60;
    
    /**
     * 验证码每日最大发送次数
     */
    public static final int SMS_CODE_MAX_DAILY_COUNT = 10;

    /**
     * 注册业务类型
     */
    public static final String BUSINESS_TYPE_REGISTER = "register";
    
    /**
     * 忘记密码业务类型
     */
    public static final String BUSINESS_TYPE_FORGET_PASSWORD = "forgetPassword";
    
    /**
     * 支付密码业务类型
     */
    public static final String BUSINESS_TYPE_PAYMENT_PASSWORD = "paymentPassword";
} 