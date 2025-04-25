package com.xuchao.ershou.service;

import com.xuchao.ershou.common.ApiResponse;
import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.model.vo.UserLoginVO;

/**
 * 验证码服务接口
 */
public interface VerificationCodeService {

    /**
     * 发送验证码
     *
     * @param phoneNumber 手机号
     * @return 发送结果
     */
    BaseResponse<String> sendVerificationCode(String phoneNumber);

    /**
     * 验证码登录
     *
     * @param phoneNumber 手机号
     * @param verificationCode 验证码
     * @return 登录结果
     */
    BaseResponse<String> loginByVerificationCode(String phoneNumber, String verificationCode);
    
    /**
     * 验证码登录（新版）
     *
     * @param phoneNumber 手机号
     * @param verificationCode 验证码
     * @return 登录结果，包含用户信息和token
     */
    ApiResponse<UserLoginVO> verifyAndLogin(String phoneNumber, String verificationCode);
    
    /**
     * 根据用户ID和验证码类型发送验证码
     * 
     * @param userId 用户ID
     * @param type 验证码类型（如：register-注册，reset_pwd-重置密码，reset_payment_pwd-重置支付密码）
     * @return 是否发送成功
     */
    boolean sendVerificationCode(Long userId, String type);
    
    /**
     * 验证验证码是否正确
     *
     * @param userId 用户ID
     * @param code 验证码
     * @param type 验证码类型
     * @return 是否验证通过
     */
    boolean verifyCode(Long userId, String code, String type);
} 