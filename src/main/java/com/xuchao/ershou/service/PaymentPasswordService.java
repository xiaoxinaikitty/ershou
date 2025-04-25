package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dao.wallet.ResetPaymentPasswordDao;
import com.xuchao.ershou.model.dto.PaymentPasswordDTO;
import com.xuchao.ershou.model.dto.ResetPasswordDTO;
import com.xuchao.ershou.model.entity.PaymentPassword;

/**
 * 支付密码Service接口
 */
public interface PaymentPasswordService {

    /**
     * 设置或修改支付密码
     *
     * @param passwordDTO 支付密码DTO
     * @return 设置结果
     */
    PaymentPassword setPaymentPassword(PaymentPasswordDTO passwordDTO);
    
    /**
     * 根据用户ID查询支付密码信息
     *
     * @param userId 用户ID
     * @return 支付密码信息
     */
    PaymentPassword getPaymentPasswordByUserId(Long userId);
    
    /**
     * 验证支付密码是否正确
     *
     * @param userId 用户ID
     * @param password 待验证的密码
     * @return 验证结果
     */
    boolean verifyPaymentPassword(Long userId, String password);
    
    /**
     * 通过验证码重置支付密码
     *
     * @param resetPasswordDTO 通过验证码重置密码DTO
     * @return 重置结果
     */
    PaymentPassword resetPaymentPassword(ResetPasswordDTO resetPasswordDTO);
    
    /**
     * 通过验证码重置支付密码
     *
     * @param resetPaymentPasswordDao 重置支付密码DAO
     * @return 重置结果
     */
    boolean resetPaymentPassword(ResetPaymentPasswordDao resetPaymentPasswordDao);
} 