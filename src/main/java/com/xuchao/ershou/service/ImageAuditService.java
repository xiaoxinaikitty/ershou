package com.xuchao.ershou.service;

import java.io.InputStream;
import java.util.Map;

/**
 * 图片内容审核服务接口
 */
public interface ImageAuditService {
    
    /**
     * 审核图片URL
     * 
     * @param imageUrl 图片URL
     * @return 是否通过审核
     */
    boolean auditImageUrl(String imageUrl);
    
    /**
     * 审核图片流
     * 
     * @param imageStream 图片输入流
     * @return 是否通过审核
     */
    boolean auditImageStream(InputStream imageStream);
} 