package com.xuchao.ershou.model.vo;

import lombok.Data;

/**
 * 发布商品位置响应对象
 */
@Data
public class ReleaseLocationVO {
    
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