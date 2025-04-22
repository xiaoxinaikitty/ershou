package com.xuchao.ershou.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件上传配置类
 */
@Configuration
public class FileConfig {

    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Value("${file.server.ip}")
    private String serverIp;
    
    @Value("${file.server.port}")
    private String serverPort;
    
    /**
     * 获取文件访问的URL前缀
     * 格式：http://serverIp:serverPort/images
     */
    @Bean
    public String fileUrlPrefix() {
        return String.format("http://%s:%s/images", serverIp, serverPort);
    }
} 