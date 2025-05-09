package com.xuchao.ershou.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 百度AI服务配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "baidu.ai")
public class BaiduAiConfig {
    /**
     * 百度AI应用ID
     */
    private String appId;
    
    /**
     * 百度AI API Key
     */
    private String apiKey;
    
    /**
     * 百度AI Secret Key
     */
    private String secretKey;
    
    /**
     * 是否启用模拟模式
     */
    private boolean mock;
} 