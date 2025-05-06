package com.xuchao.ershou.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 添加收货地址请求DTO
 */
@Data
public class UserAddressAddRequest {

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    private String consignee;

    /**
     * 所在地区
     */
    @NotBlank(message = "所在地区不能为空")
    private String region;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    private String detail;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactPhone;

    /**
     * 是否设为默认地址
     */
    private Boolean isDefault = false;
} 