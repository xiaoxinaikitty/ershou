package com.xuchao.ershou.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 验证支付密码DTO
 */
@Data
public class VerifyPasswordDTO {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 支付密码
     */
    @NotNull(message = "支付密码不能为空")
    @Size(min = 6, max = 20, message = "支付密码长度应为6-20位")
    private String paymentPassword;
} 