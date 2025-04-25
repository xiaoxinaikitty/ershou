package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.ApiResponse;
import com.xuchao.ershou.common.constant.RedisConstants;
import com.xuchao.ershou.model.dto.SmsCodeDTO;
import com.xuchao.ershou.model.dto.SmsVerifyDTO;
import com.xuchao.ershou.service.SmsService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短信控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/sms")
public class SmsController {
    
    @Autowired
    private SmsService smsService;
    
    /**
     * 发送注册验证码
     */
    @PostMapping("/register/code")
    public ApiResponse<Boolean> sendRegisterCode(@Valid @RequestBody SmsCodeDTO smsCodeDTO) {
        // 设置业务类型为注册
        smsCodeDTO.setBusinessType(RedisConstants.BUSINESS_TYPE_REGISTER);
        // 发送验证码
        boolean result = smsService.sendVerificationCode(smsCodeDTO);
        if (result) {
            return ApiResponse.success("验证码发送成功", true);
        } else {
            return ApiResponse.error(500, "验证码发送失败，请稍后重试");
        }
    }
    
    /**
     * 发送忘记密码验证码
     */
    @PostMapping("/forgetPassword/code")
    public ApiResponse<Boolean> sendForgetPasswordCode(@Valid @RequestBody SmsCodeDTO smsCodeDTO) {
        // 设置业务类型为忘记密码
        smsCodeDTO.setBusinessType(RedisConstants.BUSINESS_TYPE_FORGET_PASSWORD);
        // 发送验证码
        boolean result = smsService.sendVerificationCode(smsCodeDTO);
        if (result) {
            return ApiResponse.success("验证码发送成功", true);
        } else {
            return ApiResponse.error(500, "验证码发送失败，请稍后重试");
        }
    }
    
    /**
     * 发送支付密码验证码
     */
    @PostMapping("/paymentPassword/code")
    public ApiResponse<Boolean> sendPaymentPasswordCode(@Valid @RequestBody SmsCodeDTO smsCodeDTO) {
        // 设置业务类型为支付密码
        smsCodeDTO.setBusinessType(RedisConstants.BUSINESS_TYPE_PAYMENT_PASSWORD);
        // 发送验证码
        boolean result = smsService.sendVerificationCode(smsCodeDTO);
        if (result) {
            return ApiResponse.success("验证码发送成功", true);
        } else {
            return ApiResponse.error(500, "验证码发送失败，请稍后重试");
        }
    }
    
    /**
     * 验证验证码
     */
    @PostMapping("/verify")
    public ApiResponse<Boolean> verifyCode(@Valid @RequestBody SmsVerifyDTO smsVerifyDTO) {
        // 验证验证码
        boolean result = smsService.verifyCode(smsVerifyDTO);
        if (result) {
            return ApiResponse.success("验证码验证成功", true);
        } else {
            return ApiResponse.error(400, "验证码验证失败，请检查验证码是否正确");
        }
    }
} 