package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.common.ApiResponse;
import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.JwtUtil;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.mapper.UserMapper;
import com.xuchao.ershou.mapper.VerificationCodeMapper;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.model.entity.VerificationCodeRecord;
import com.xuchao.ershou.model.entity.VerificationHistory;
import com.xuchao.ershou.model.vo.UserLoginVO;
import com.xuchao.ershou.service.VerificationCodeService;
import com.xuchao.ershou.utils.SmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * 验证码服务实现类
 */
@Slf4j
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Autowired
    private VerificationCodeMapper verificationCodeMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private SmsUtils smsUtils;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 验证码有效期（分钟）
     */
    private static final int CODE_EXPIRE_MINUTES = 5;
    
    /**
     * 最大验证尝试次数
     */
    private static final int MAX_ATTEMPT_COUNT = 5;

    @Override
    public BaseResponse<String> sendVerificationCode(String phoneNumber) {
        // 1. 校验手机号格式（已在DTO中使用注解校验）
        
        // 2. 生成6位随机验证码
        String verificationCode = generateSixDigitCode();
        
        // 3. 保存验证码记录
        VerificationCodeRecord record = new VerificationCodeRecord();
        record.setPhoneNumber(phoneNumber);
        record.setVerificationCode(verificationCode);
        record.setSendTime(LocalDateTime.now());
        record.setExpirationTime(LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES));
        record.setIsUsed(false);
        record.setAttemptCount(0);
        
        try {
            verificationCodeMapper.insertVerificationCode(record);
            
            // 4. 发送短信
            boolean sendResult = smsUtils.sendVerificationCode(phoneNumber, verificationCode);
            if (sendResult) {
                return ResultUtils.success("验证码发送成功");
            } else {
                return ResultUtils.error(ErrorCode.OPERATION_ERROR.getCode(), "验证码发送失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("发送验证码异常", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR.getCode(), "系统错误，请稍后重试");
        }
    }

    @Override
    @Transactional
    public BaseResponse<String> loginByVerificationCode(String phoneNumber, String verificationCode) {
        // 1. 校验参数（已在DTO中使用注解校验）
        
        try {
            // 2. 获取最新的有效验证码
            VerificationCodeRecord record = verificationCodeMapper.getLatestValidCode(phoneNumber);
            
            // 3. 验证码不存在或已过期
            if (record == null || LocalDateTime.now().isAfter(record.getExpirationTime())) {
                saveVerificationHistory(phoneNumber, verificationCode, false, record != null ? record.getId() : null);
                return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "验证码已过期，请重新获取");
            }
            
            // 4. 验证码已使用
            if (record.getIsUsed()) {
                saveVerificationHistory(phoneNumber, verificationCode, false, record.getId());
                return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "验证码已使用，请重新获取");
            }
            
            // 5. 超过最大尝试次数
            if (record.getAttemptCount() >= MAX_ATTEMPT_COUNT) {
                saveVerificationHistory(phoneNumber, verificationCode, false, record.getId());
                return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "验证码尝试次数过多，请重新获取");
            }
            
            // 6. 验证码不匹配
            if (!record.getVerificationCode().equals(verificationCode)) {
                // 增加尝试次数
                verificationCodeMapper.incrementAttemptCount(record.getId());
                saveVerificationHistory(phoneNumber, verificationCode, false, record.getId());
                return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "验证码错误");
            }
            
            // 7. 验证码验证成功，标记为已使用
            verificationCodeMapper.updateCodeUsedStatus(record.getId());
            saveVerificationHistory(phoneNumber, verificationCode, true, record.getId());
            
            // 8. 查询用户是否存在，不存在则自动注册
            User user = userMapper.getUserByPhone(phoneNumber);
            if (user == null) {
                // 自动注册用户
                user = new User();
                user.setUsername("user_" + phoneNumber.substring(phoneNumber.length() - 4));
                user.setPhone(phoneNumber);
                user.setPassword(""); // 空密码，只能使用验证码登录
                user.setRole("普通用户");
                user.setCreateTime(LocalDateTime.now());
                userMapper.insertUser(user);
                user = userMapper.getUserByPhone(phoneNumber); // 获取插入后的用户信息
            }
            
            // 9. 生成JWT令牌
            String token = jwtUtil.generateToken(user.getUserId(), user.getUsername());
            
            return ResultUtils.success(token);
        } catch (Exception e) {
            log.error("验证码登录异常", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR.getCode(), "系统错误，请稍后重试");
        }
    }
    
    @Override
    @Transactional
    public ApiResponse<UserLoginVO> verifyAndLogin(String phoneNumber, String verificationCode) {
        try {
            // 1. 获取最新的有效验证码
            VerificationCodeRecord record = verificationCodeMapper.getLatestValidCode(phoneNumber);
            
            // 2. 验证码不存在或已过期
            if (record == null || LocalDateTime.now().isAfter(record.getExpirationTime())) {
                saveVerificationHistory(phoneNumber, verificationCode, false, record != null ? record.getId() : null);
                return ApiResponse.error(400, "验证码已过期，请重新获取");
            }
            
            // 3. 验证码已使用
            if (record.getIsUsed()) {
                saveVerificationHistory(phoneNumber, verificationCode, false, record.getId());
                return ApiResponse.error(400, "验证码已使用，请重新获取");
            }
            
            // 4. 超过最大尝试次数
            if (record.getAttemptCount() >= MAX_ATTEMPT_COUNT) {
                saveVerificationHistory(phoneNumber, verificationCode, false, record.getId());
                return ApiResponse.error(400, "验证码已过期或已使用");
            }
            
            // 5. 验证码不匹配
            if (!record.getVerificationCode().equals(verificationCode)) {
                // 增加尝试次数
                verificationCodeMapper.incrementAttemptCount(record.getId());
                saveVerificationHistory(phoneNumber, verificationCode, false, record.getId());
                return ApiResponse.error(400, "验证码错误、已过期或已使用");
            }
            
            // 6. 验证码验证成功，标记为已使用
            verificationCodeMapper.updateCodeUsedStatus(record.getId());
            saveVerificationHistory(phoneNumber, verificationCode, true, record.getId());
            
            // 7. 查询用户是否存在，不存在则自动注册
            User user = userMapper.getUserByPhone(phoneNumber);
            if (user == null) {
                // 自动注册用户
                user = new User();
                user.setUsername("user_" + phoneNumber.substring(phoneNumber.length() - 4));
                user.setPhone(phoneNumber);
                user.setPassword(""); // 空密码，只能使用验证码登录
                user.setRole("普通用户");
                user.setCreateTime(LocalDateTime.now());
                userMapper.insertUser(user);
                user = userMapper.getUserByPhone(phoneNumber); // 获取插入后的用户信息
            }
            
            // 8. 生成JWT令牌
            String token = jwtUtil.generateToken(user.getUserId(), user.getUsername());
            
            // 9. 构建并返回用户登录信息
            UserLoginVO loginVO = new UserLoginVO();
            loginVO.setUserId(user.getUserId());
            loginVO.setUsername(user.getUsername());
            loginVO.setRole(user.getRole());
            loginVO.setToken(token);
            
            return ApiResponse.success("登录成功", loginVO);
        } catch (Exception e) {
            log.error("验证码登录异常", e);
            return ApiResponse.error(500, "系统错误，请稍后重试");
        }
    }
    
    @Override
    public boolean sendVerificationCode(Long userId, String type) {
        // 参数校验
        if (userId == null || type == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询用户信息
        User user = userMapper.getUserById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        
        // 获取联系方式（手机或邮箱）
        String phoneNumber = user.getPhone();
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户未设置手机号，无法发送验证码");
        }
        
        // 生成6位随机验证码
        String verificationCode = generateSixDigitCode();
        
        // 保存验证码记录
        VerificationCodeRecord record = new VerificationCodeRecord();
        record.setPhoneNumber(phoneNumber);
        record.setVerificationCode(verificationCode);
        record.setSendTime(LocalDateTime.now());
        record.setExpirationTime(LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES));
        record.setIsUsed(false);
        record.setAttemptCount(0);
        
        try {
            verificationCodeMapper.insertVerificationCode(record);
            
            // 发送短信
            boolean sendResult = smsUtils.sendVerificationCode(phoneNumber, verificationCode);
            if (!sendResult) {
                log.error("验证码短信发送失败，用户ID: {}, 类型: {}", userId, type);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "短信发送失败");
            }
            
            log.info("向用户 {} 发送验证码：类型：{}", userId, type);
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("发送验证码异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统错误：" + e.getMessage());
        }
    }
    
    @Override
    public boolean verifyCode(Long userId, String code, String type) {
        // 参数校验
        if (userId == null || code == null || type == null) {
            return false;
        }
        
        try {
            // 查询用户信息
            User user = userMapper.getUserById(userId);
            if (user == null) {
                return false;
            }
            
            // 获取用户手机号
            String phoneNumber = user.getPhone();
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                return false;
            }
            
            // 获取最新的有效验证码
            VerificationCodeRecord record = verificationCodeMapper.getLatestValidCode(phoneNumber);
            
            // 验证码不存在或已过期
            if (record == null || LocalDateTime.now().isAfter(record.getExpirationTime())) {
                saveVerificationHistory(phoneNumber, code, false, record != null ? record.getId() : null);
                return false;
            }
            
            // 验证码已使用
            if (record.getIsUsed()) {
                saveVerificationHistory(phoneNumber, code, false, record.getId());
                return false;
            }
            
            // 超过最大尝试次数
            if (record.getAttemptCount() >= MAX_ATTEMPT_COUNT) {
                saveVerificationHistory(phoneNumber, code, false, record.getId());
                return false;
            }
            
            // 验证码不匹配
            if (!record.getVerificationCode().equals(code)) {
                // 增加尝试次数
                verificationCodeMapper.incrementAttemptCount(record.getId());
                saveVerificationHistory(phoneNumber, code, false, record.getId());
                return false;
            }
            
            // 验证码验证成功，标记为已使用
            verificationCodeMapper.updateCodeUsedStatus(record.getId());
            saveVerificationHistory(phoneNumber, code, true, record.getId());
            
            return true;
        } catch (Exception e) {
            log.error("验证码验证异常", e);
            return false;
        }
    }
    
    /**
     * 生成6位数字验证码
     */
    private String generateSixDigitCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 生成100000-999999之间的随机数
        return String.valueOf(code);
    }
    
    /**
     * 保存验证历史记录
     */
    private void saveVerificationHistory(String phoneNumber, String verificationCode, boolean result, Long recordId) {
        VerificationHistory history = new VerificationHistory();
        history.setPhoneNumber(phoneNumber);
        history.setVerificationCode(verificationCode);
        history.setVerificationResult(result);
        history.setVerificationTime(LocalDateTime.now());
        history.setRelatedRecordId(recordId);
        
        try {
            verificationCodeMapper.insertVerificationHistory(history);
        } catch (Exception e) {
            log.error("保存验证历史记录异常", e);
        }
    }
} 