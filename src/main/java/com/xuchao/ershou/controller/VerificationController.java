package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.ApiResponse;
import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.model.dto.SendVerificationCodeDTO;
import com.xuchao.ershou.model.dto.VerificationLoginDTO;
import com.xuchao.ershou.model.vo.UserLoginVO;
import com.xuchao.ershou.service.VerificationCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * 验证码相关接口控制器
 */
@Slf4j
@RestController
@RequestMapping("/verification")
public class VerificationController {

    @Autowired
    private VerificationCodeService verificationCodeService;

    /**
     * 发送验证码
     * @param sendVerificationCodeDTO 发送验证码请求DTO
     * @return 发送结果
     */
    @PostMapping("/code/send")
    public BaseResponse<String> sendVerificationCode(@RequestBody @Valid SendVerificationCodeDTO sendVerificationCodeDTO) {
        if (sendVerificationCodeDTO == null || sendVerificationCodeDTO.getPhoneNumber() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "参数错误");
        }
        
        return verificationCodeService.sendVerificationCode(sendVerificationCodeDTO.getPhoneNumber());
    }
    
    /**
     * 验证码登录
     * @param verificationLoginDTO 验证码登录请求DTO
     * @return 登录结果
     */
    @PostMapping("/login")
    public BaseResponse<String> loginByVerificationCode(@RequestBody @Valid VerificationLoginDTO verificationLoginDTO) {
        if (verificationLoginDTO == null 
                || verificationLoginDTO.getPhoneNumber() == null 
                || verificationLoginDTO.getVerificationCode() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "参数错误");
        }
        
        return verificationCodeService.loginByVerificationCode(
                verificationLoginDTO.getPhoneNumber(), 
                verificationLoginDTO.getVerificationCode());
    }
    
    /**
     * 验证码登录（新版）
     * @param verificationLoginDTO 验证码登录请求DTO
     * @return 登录结果，包含用户信息和token
     */
    @PostMapping("/verify-login")
    public ApiResponse<UserLoginVO> verifyAndLogin(@RequestBody @Valid VerificationLoginDTO verificationLoginDTO) {
        if (verificationLoginDTO == null 
                || verificationLoginDTO.getPhoneNumber() == null 
                || verificationLoginDTO.getVerificationCode() == null) {
            return ApiResponse.error(400, "参数错误");
        }
        
        return verificationCodeService.verifyAndLogin(
                verificationLoginDTO.getPhoneNumber(), 
                verificationLoginDTO.getVerificationCode());
    }
} 