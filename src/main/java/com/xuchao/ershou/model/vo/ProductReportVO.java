package com.xuchao.ershou.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商品举报视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReportVO {
    /**
     * 举报ID
     */
    private Long reportId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品标题
     */
    private String productTitle;
    
    /**
     * 举报用户ID
     */
    private Long userId;
    
    /**
     * 举报类型(1虚假商品 2违禁品 3侵权等)
     */
    private Integer reportType;
    
    /**
     * 举报内容
     */
    private String reportContent;
    
    /**
     * 处理状态(0未处理 1已处理)
     */
    private Integer status;
    
    /**
     * 举报时间
     */
    private LocalDateTime createdTime;
}
