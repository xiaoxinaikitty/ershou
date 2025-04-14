package com.xuchao.ershou.model.vo;

import lombok.Data;

/**
 * 订单地址响应视图对象
 */
@Data
public class OrderAddressVO {
    
    /**
     * 收货人姓名
     */
    private String receiverName;
    
    /**
     * 收货人电话
     */
    private String receiverPhone;
    
    /**
     * 省份
     */
    private String province;
    
    /**
     * 城市
     */
    private String city;
    
    /**
     * 区/县
     */
    private String district;
    
    /**
     * 详细地址
     */
    private String detailAddress;
}