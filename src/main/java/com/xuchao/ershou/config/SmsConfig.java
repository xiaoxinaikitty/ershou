package com.xuchao.ershou.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 短信服务配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.sms")
public class SmsConfig {
    /**
     * 阿里云AccessKey ID
     */
    private String accessKeyId;
    
    /**
     * 阿里云AccessKey Secret
     */
    private String accessKeySecret;
    
    /**
     * 短信签名
     */
    private String signName;
    
    /**
     * 模板代码
     */
    private TemplateCode templateCode;
    
    /**
     * 模板代码内部类
     */
    @Data
    public static class TemplateCode {
        /**
         * 注册模板代码
         */
        private String register;
        
        /**
         * 忘记密码模板代码
         */
        private String forgetPassword;
        
        /**
         * 支付密码模板代码
         */
        private String paymentPassword;
    }
} 