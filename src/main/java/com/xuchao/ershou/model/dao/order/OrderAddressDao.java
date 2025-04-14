package com.xuchao.ershou.model.dao.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 订单收货地址数据传输对象
 */
@Data
public class OrderAddressDao {
    
    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名最多50个字符")
    private String receiverName;
    
    /**
     * 收货人电话
     */
    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String receiverPhone;
    
    /**
     * 省份
     */
    @NotBlank(message = "省份不能为空")
    @Size(max = 30, message = "省份最多30个字符")
    private String province;
    
    /**
     * 城市
     */
    @NotBlank(message = "城市不能为空")
    @Size(max = 30, message = "城市最多30个字符")
    private String city;
    
    /**
     * 区/县
     */
    @NotBlank(message = "区/县不能为空")
    @Size(max = 30, message = "区/县最多30个字符")
    private String district;
    
    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 200, message = "详细地址最多200个字符")
    private String detailAddress;
}