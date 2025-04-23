package com.xuchao.ershou.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 支付密码设置/修改DTO
 */
@Data
public class PaymentPasswordDTO {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 新支付密码
     */
    @NotNull(message = "支付密码不能为空")
    @Size(min = 6, max = 20, message = "支付密码长度应为6-20位")
    private String paymentPassword;

    /**
     * 原支付密码（修改密码时必填）
     */
    private String oldPaymentPassword;
} 