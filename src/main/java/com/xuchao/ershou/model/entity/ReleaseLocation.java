package com.xuchao.ershou.model.entity;

import lombok.Data;

/**
 * 发布商品位置实体
 */
@Data
public class ReleaseLocation {
    
    /**
     * 位置记录ID
     */
    private Integer id;
    
    /**
     * 省份名称
     */
    private String province;
    
    /**
     * 城市名称
     */
    private String city;
    
    /**
     * 区/县名称
     */
    private String district;
} 