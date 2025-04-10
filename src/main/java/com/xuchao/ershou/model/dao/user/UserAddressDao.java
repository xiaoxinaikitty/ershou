package com.xuchao.ershou.model.dao.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserAddressDao {
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    
    @NotBlank(message = "收货人不能为空")
    private String consignee;
    
    @NotBlank(message = "地区信息不能为空")
    private String region;
    
    @NotBlank(message = "详细地址不能为空")
    private String detail;
    
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;
    
    @NotNull(message = "默认地址状态不能为空")
    private Boolean isDefault;

    // getters and setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}