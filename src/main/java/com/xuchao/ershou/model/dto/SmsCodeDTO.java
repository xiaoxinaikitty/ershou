package com.xuchao.ershou.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 短信验证码请求DTO
 */
@Data
public class SmsCodeDTO {
    /**
     * 手机号码
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNumber;
    
    /**
     * 业务类型：register-注册, forgetPassword-忘记密码, paymentPassword-支付密码
     */
    @NotBlank(message = "业务类型不能为空")
    @Pattern(regexp = "^(register|forgetPassword|paymentPassword)$", message = "业务类型不正确")
    private String businessType;
} 