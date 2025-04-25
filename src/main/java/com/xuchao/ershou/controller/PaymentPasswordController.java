package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.ApiResponse;
import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dao.wallet.ResetPaymentPasswordDao;
import com.xuchao.ershou.model.dto.PaymentPasswordDTO;
import com.xuchao.ershou.model.dto.ResetPasswordDTO;
import com.xuchao.ershou.model.dto.VerifyPasswordDTO;
import com.xuchao.ershou.model.entity.PaymentPassword;
import com.xuchao.ershou.service.PaymentPasswordService;
import com.xuchao.ershou.service.VerificationCodeService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付密码相关接口
 */
@RestController
@RequestMapping("/api/wallet/payment-password")
public class PaymentPasswordController {

    @Resource
    private PaymentPasswordService paymentPasswordService;
    
    @Resource
    private VerificationCodeService verificationCodeService;
    
    // 支付密码重置的验证码类型
    private static final String RESET_PAYMENT_PWD_CODE_TYPE = "reset_payment_pwd";

    /**
     * 设置或修改支付密码
     *
     * @param passwordDTO 支付密码DTO
     * @return 操作结果
     */
    @PostMapping("/set")
    public BaseResponse<String> setPaymentPassword(@RequestBody @Valid PaymentPasswordDTO passwordDTO) {
        // 参数校验
        if (passwordDTO == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "参数不完整");
        }
        
        if (passwordDTO.getUserId() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "用户ID不能为空");
        }
        
        if (!StringUtils.hasText(passwordDTO.getPaymentPassword())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "支付密码不能为空");
        }
        
        // 支付密码长度校验
        if (passwordDTO.getPaymentPassword().length() < 6 || passwordDTO.getPaymentPassword().length() > 20) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "支付密码长度应为6-20位");
        }
        
        try {
            // 查询是否已有支付密码
            PaymentPassword existPassword = paymentPasswordService.getPaymentPasswordByUserId(passwordDTO.getUserId());
            
            // 设置或修改支付密码
            paymentPasswordService.setPaymentPassword(passwordDTO);
            
            // 根据是否已有支付密码返回不同的成功提示
            if (existPassword != null) {
                return ResultUtils.success("支付密码修改成功");
            } else {
                return ResultUtils.success("支付密码设置成功");
            }
        } catch (BusinessException e) {
            return ResultUtils.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR.getCode(), "系统错误：" + e.getMessage());
        }
    }
    
    /**
     * 验证支付密码
     *
     * @param verifyPasswordDTO 验证支付密码DTO
     * @return 验证结果
     */
    @PostMapping("/verify")
    public BaseResponse<Boolean> verifyPaymentPassword(@RequestBody @Valid VerifyPasswordDTO verifyPasswordDTO) {
        // 参数校验
        if (verifyPasswordDTO == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "参数不完整");
        }
        
        if (verifyPasswordDTO.getUserId() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "用户ID不能为空");
        }
        
        if (!StringUtils.hasText(verifyPasswordDTO.getPaymentPassword())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "支付密码不能为空");
        }
        
        try {
            // 查询用户是否设置了支付密码
            PaymentPassword paymentPassword = paymentPasswordService.getPaymentPasswordByUserId(verifyPasswordDTO.getUserId());
            if (paymentPassword == null) {
                return ResultUtils.error(ErrorCode.NOT_FOUND.getCode(), "用户未设置支付密码");
            }
            
            // 验证支付密码
            boolean isMatch = paymentPasswordService.verifyPaymentPassword(
                    verifyPasswordDTO.getUserId(), 
                    verifyPasswordDTO.getPaymentPassword()
            );
            
            // 根据验证结果返回不同的响应
            if (isMatch) {
                return ResultUtils.success(true);
            } else {
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("verified", false);
                errorData.put("message", "支付密码不正确");
                return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "支付密码不正确", false);
            }
        } catch (BusinessException e) {
            return ResultUtils.error(e.getCode(), e.getMessage(), false);
        } catch (Exception e) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR.getCode(), "系统错误：" + e.getMessage(), false);
        }
    }
    
    /**
     * 发送重置支付密码的验证码
     *
     * @param userId 用户ID
     * @return 发送结果
     */
    @GetMapping("/send-reset-code")
    public BaseResponse<String> sendResetCode(@RequestParam("userId") Long userId) {
        // 参数校验
        if (userId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "用户ID不能为空");
        }
        
        try {
            // 发送验证码
            verificationCodeService.sendVerificationCode(userId, RESET_PAYMENT_PWD_CODE_TYPE);
            return ResultUtils.success("验证码发送成功，请查收手机或邮箱");
        } catch (BusinessException e) {
            return ResultUtils.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR.getCode(), "系统错误：" + e.getMessage());
        }
    }
    
    /**
     * 重置支付密码
     *
     * @param resetPasswordDTO 重置密码DTO
     * @return 重置结果
     */
    @PostMapping("/reset")
    public BaseResponse<String> resetPaymentPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
        // 参数校验
        if (resetPasswordDTO == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "参数不完整");
        }
        
        if (resetPasswordDTO.getUserId() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "用户ID不能为空");
        }
        
        if (!StringUtils.hasText(resetPasswordDTO.getNewPaymentPassword())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "新支付密码不能为空");
        }
        
        if (!StringUtils.hasText(resetPasswordDTO.getVerificationCode())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "验证码不能为空");
        }
        
        try {
            // 重置支付密码
            paymentPasswordService.resetPaymentPassword(resetPasswordDTO);
            return ResultUtils.success("支付密码重置成功");
        } catch (BusinessException e) {
            return ResultUtils.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR.getCode(), "系统错误：" + e.getMessage());
        }
    }

    /**
     * 通过手机验证码重置支付密码
     *
     * @param resetPaymentPasswordDao 重置支付密码DAO
     * @return 重置结果
     */
    @PostMapping("/reset-by-sms")
    public ApiResponse<Boolean> resetPaymentPasswordBySms(@RequestBody @Valid ResetPaymentPasswordDao resetPaymentPasswordDao) {
        try {
            boolean result = paymentPasswordService.resetPaymentPassword(resetPaymentPasswordDao);
            if (result) {
                return ApiResponse.success("支付密码重置成功", true);
            } else {
                return ApiResponse.error(500, "支付密码重置失败");
            }
        } catch (BusinessException e) {
            return ApiResponse.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(ErrorCode.SYSTEM_ERROR.getCode(), "系统错误：" + e.getMessage());
        }
    }
} 