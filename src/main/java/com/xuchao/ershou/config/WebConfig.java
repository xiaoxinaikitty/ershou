package com.xuchao.ershou.config;

import com.xuchao.ershou.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Value("${file.upload.path}")
    private String uploadPath;

    public WebConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration() {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtAuthenticationFilter);
        registration.addUrlPatterns("/*");
        registration.setName("jwtAuthenticationFilter");
        registration.setOrder(1);
        return registration;
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保文件上传目录存在
        ensureDirectoryExists(uploadPath);
        
        // 映射本地文件夹到URL路径，使上传的图片可访问
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
    
    /**
     * 确保目录存在，如果不存在则创建
     * @param directory 目录路径
     */
    private void ensureDirectoryExists(String directory) {
        java.io.File dir = new java.io.File(directory);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new RuntimeException("无法创建目录: " + directory);
            }
        }
    }
}