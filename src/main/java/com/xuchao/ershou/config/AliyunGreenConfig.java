package com.xuchao.ershou.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云内容安全配置类
 */
@Configuration
public class AliyunGreenConfig {

    @Value("${aliyun.accessKey.id}")
    private String accessKeyId;

    @Value("${aliyun.accessKey.secret}")
    private String accessKeySecret;

    @Value("${aliyun.green.region}")
    private String regionId;

    /**
     * 创建阿里云内容安全客户端
     */
    @Bean
    public IAcsClient acsClient() {
        DefaultProfile profile = DefaultProfile.getProfile(
                regionId,
                accessKeyId,
                accessKeySecret);
        return new DefaultAcsClient(profile);
    }
} 