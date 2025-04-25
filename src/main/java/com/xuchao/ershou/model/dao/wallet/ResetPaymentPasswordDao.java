package com.xuchao.ershou.model.dao.wallet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 重置支付密码DAO
 */
@Data
public class ResetPaymentPasswordDao {
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNumber;
    
    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须是6位数字")
    private String code;
    
    /**
     * 新支付密码
     */
    @NotBlank(message = "支付密码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "支付密码必须是6位数字")
    private String newPaymentPassword;
    
    /**
     * 确认新支付密码
     */
    @NotBlank(message = "确认支付密码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "支付密码必须是6位数字")
    private String confirmPaymentPassword;
} 