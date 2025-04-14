package com.xuchao.ershou.model.vo;

import lombok.Data;

/**
 * 交易方式视图对象
 */
@Data
public class RecycleMethodVO {
    
    /**
     * 方式ID
     */
    private Integer methodId;
    
    /**
     * 方式名称
     */
    private String methodName;
    
    /**
     * 方式描述
     */
    private String methodDesc;
}