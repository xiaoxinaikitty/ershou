package com.xuchao.ershou.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 重置支付密码DTO
 */
@Data
public class ResetPasswordDTO {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 新支付密码
     */
    @NotBlank(message = "新支付密码不能为空")
    @Size(min = 6, max = 20, message = "支付密码长度应为6-20位")
    private String newPaymentPassword;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Size(min = 4, max = 8, message = "验证码格式错误")
    private String verificationCode;
} 