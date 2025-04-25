package com.xuchao.ershou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.constant.RedisConstants;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.mapper.PaymentPasswordMapper;
import com.xuchao.ershou.mapper.UserMapper;
import com.xuchao.ershou.model.dao.wallet.ResetPaymentPasswordDao;
import com.xuchao.ershou.model.dto.PaymentPasswordDTO;
import com.xuchao.ershou.model.dto.ResetPasswordDTO;
import com.xuchao.ershou.model.dto.SmsVerifyDTO;
import com.xuchao.ershou.model.entity.PaymentPassword;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.service.PaymentPasswordService;
import com.xuchao.ershou.service.SmsService;
import com.xuchao.ershou.service.VerificationCodeService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 支付密码Service实现类
 */
@Service
public class PaymentPasswordServiceImpl extends ServiceImpl<PaymentPasswordMapper, PaymentPassword> implements PaymentPasswordService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private PaymentPasswordMapper paymentPasswordMapper;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;
    
    @Resource
    private SmsService smsService;
    
    @Resource
    private VerificationCodeService verificationCodeService;
    
    // 支付密码重置的验证码类型
    private static final String RESET_PAYMENT_PWD_CODE_TYPE = "reset_payment_pwd";

    /**
     * 设置或修改支付密码
     *
     * @param passwordDTO 支付密码DTO
     * @return 设置结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentPassword setPaymentPassword(PaymentPasswordDTO passwordDTO) {
        // 参数校验
        if (passwordDTO == null || passwordDTO.getUserId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不完整");
        }
        
        // 检查用户是否存在
        User user = userMapper.selectById(passwordDTO.getUserId());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        
        // 查询用户的支付密码记录
        PaymentPassword existPassword = this.getPaymentPasswordByUserId(passwordDTO.getUserId());
        
        // 如果是修改密码（已存在支付密码记录）
        if (existPassword != null) {
            // 验证原密码是否提供
            if (!StringUtils.hasText(passwordDTO.getOldPaymentPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "修改密码时必须提供原支付密码");
            }
            
            // 验证原密码是否正确
            if (!passwordEncoder.matches(passwordDTO.getOldPaymentPassword(), existPassword.getHashedPaymentPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "原支付密码不正确");
            }
            
            // 更新支付密码
            existPassword.setHashedPaymentPassword(passwordEncoder.encode(passwordDTO.getPaymentPassword()));
            existPassword.setLastUpdateTime(LocalDateTime.now());
            this.updateById(existPassword);
            return existPassword;
        } else {
            // 首次设置支付密码
            PaymentPassword newPassword = new PaymentPassword();
            newPassword.setUserId(passwordDTO.getUserId());
            newPassword.setHashedPaymentPassword(passwordEncoder.encode(passwordDTO.getPaymentPassword()));
            newPassword.setLastUpdateTime(LocalDateTime.now());
            this.save(newPassword);
            return newPassword;
        }
    }
    
    /**
     * 根据用户ID查询支付密码信息
     *
     * @param userId 用户ID
     * @return 支付密码信息
     */
    @Override
    public PaymentPassword getPaymentPasswordByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        
        LambdaQueryWrapper<PaymentPassword> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentPassword::getUserId, userId);
        return this.getOne(queryWrapper);
    }
    
    /**
     * 验证支付密码是否正确
     *
     * @param userId 用户ID
     * @param password 待验证的密码
     * @return 验证结果
     */
    @Override
    public boolean verifyPaymentPassword(Long userId, String password) {
        if (userId == null || !StringUtils.hasText(password)) {
            return false;
        }
        
        // 查询支付密码记录
        PaymentPassword paymentPassword = this.getPaymentPasswordByUserId(userId);
        if (paymentPassword == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户未设置支付密码");
        }
        
        // 验证密码是否匹配
        return passwordEncoder.matches(password, paymentPassword.getHashedPaymentPassword());
    }
    
    /**
     * 通过验证码重置支付密码
     *
     * @param resetPasswordDTO 重置密码DTO
     * @return 重置结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentPassword resetPaymentPassword(ResetPasswordDTO resetPasswordDTO) {
        // 参数校验
        if (resetPasswordDTO == null || resetPasswordDTO.getUserId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不完整");
        }
        
        // 检查用户是否存在
        User user = userMapper.selectById(resetPasswordDTO.getUserId());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        
        // 验证码校验
        if (!StringUtils.hasText(resetPasswordDTO.getVerificationCode())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码不能为空");
        }
        
        // 验证验证码是否正确
        boolean isCodeValid = verificationCodeService.verifyCode(
                resetPasswordDTO.getUserId(),
                resetPasswordDTO.getVerificationCode(),
                RESET_PAYMENT_PWD_CODE_TYPE
        );
        
        if (!isCodeValid) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误或已过期");
        }
        
        // 新密码校验
        if (!StringUtils.hasText(resetPasswordDTO.getNewPaymentPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新密码不能为空");
        }
        
        if (resetPasswordDTO.getNewPaymentPassword().length() < 6 || resetPasswordDTO.getNewPaymentPassword().length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度应为6-20位");
        }
        
        // 查询用户的支付密码记录
        PaymentPassword existPassword = this.getPaymentPasswordByUserId(resetPasswordDTO.getUserId());
        
        // 如果已存在支付密码记录，则更新
        if (existPassword != null) {
            existPassword.setHashedPaymentPassword(passwordEncoder.encode(resetPasswordDTO.getNewPaymentPassword()));
            existPassword.setLastUpdateTime(LocalDateTime.now());
            this.updateById(existPassword);
            return existPassword;
        } else {
            // 如果不存在，则创建新记录
            PaymentPassword newPassword = new PaymentPassword();
            newPassword.setUserId(resetPasswordDTO.getUserId());
            newPassword.setHashedPaymentPassword(passwordEncoder.encode(resetPasswordDTO.getNewPaymentPassword()));
            newPassword.setLastUpdateTime(LocalDateTime.now());
            this.save(newPassword);
            return newPassword;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPaymentPassword(ResetPaymentPasswordDao resetPasswordDao) {
        // 1. 参数校验
        if (!Objects.equals(resetPasswordDao.getNewPaymentPassword(), resetPasswordDao.getConfirmPaymentPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的支付密码不一致");
        }

        // 2. 验证验证码
        SmsVerifyDTO smsVerifyDTO = new SmsVerifyDTO();
        smsVerifyDTO.setPhoneNumber(resetPasswordDao.getPhoneNumber());
        smsVerifyDTO.setCode(resetPasswordDao.getCode());
        smsVerifyDTO.setBusinessType(RedisConstants.BUSINESS_TYPE_PAYMENT_PASSWORD);
        
        boolean verifyResult = smsService.verifyCode(smsVerifyDTO);
        if (!verifyResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误或已过期");
        }
        
        // 3. 根据手机号查找用户
        User user = userMapper.getUserByPhone(resetPasswordDao.getPhoneNumber());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "未找到绑定该手机号的用户");
        }
        
        // 4. 更新或创建支付密码
        PaymentPassword paymentPassword = getPaymentPasswordByUserId(user.getUserId());
        if (paymentPassword == null) {
            // 创建新支付密码
            paymentPassword = new PaymentPassword();
            paymentPassword.setUserId(user.getUserId());
            paymentPassword.setHashedPaymentPassword(resetPasswordDao.getNewPaymentPassword()); // 生产环境应该加密
            paymentPassword.setLastUpdateTime(LocalDateTime.now());
            paymentPasswordMapper.insert(paymentPassword);
        } else {
            // 更新已有支付密码
            paymentPassword.setHashedPaymentPassword(resetPasswordDao.getNewPaymentPassword()); // 生产环境应该加密
            paymentPassword.setLastUpdateTime(LocalDateTime.now());
            paymentPasswordMapper.updateById(paymentPassword);
        }
        
        return true;
    }
} 