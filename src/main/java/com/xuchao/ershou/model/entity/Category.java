package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品分类实体类
 */
@Data
@TableName("category")
public class Category implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 分类ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 分类描述
     */
    private String description;
    
    /**
     * 分类图标URL
     */
    private String iconUrl;
    
    /**
     * 排序值
     */
    private Integer sortOrder;
    
    /**
     * 是否启用
     */
    private Boolean isEnabled;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
} 