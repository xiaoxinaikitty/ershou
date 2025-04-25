package com.xuchao.ershou.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuchao.ershou.common.constant.RedisConstants;
import com.xuchao.ershou.config.SmsConfig;
import com.xuchao.ershou.mapper.VerificationCodeRecordMapper;
import com.xuchao.ershou.model.dto.SmsCodeDTO;
import com.xuchao.ershou.model.dto.SmsVerifyDTO;
import com.xuchao.ershou.model.entity.VerificationCodeRecord;
import com.xuchao.ershou.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 短信服务实现类
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {
    
    @Autowired
    private SmsConfig smsConfig;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private VerificationCodeRecordMapper verificationCodeRecordMapper;
    
    /**
     * 发送验证码
     *
     * @param smsCodeDTO 短信验证码DTO
     * @return 发送结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendVerificationCode(SmsCodeDTO smsCodeDTO) {
        String phoneNumber = smsCodeDTO.getPhoneNumber();
        String businessType = smsCodeDTO.getBusinessType();
        
        // 1. 检查发送频率限制
        String dailyCountKey = RedisConstants.SMS_CODE_DAILY_COUNT_PREFIX + phoneNumber;
        String countStr = redisTemplate.opsForValue().get(dailyCountKey);
        int count = countStr == null ? 0 : Integer.parseInt(countStr);
        
        if (count >= RedisConstants.SMS_CODE_MAX_DAILY_COUNT) {
            log.warn("手机号[{}]超过每日短信发送限制", phoneNumber);
            return false;
        }
        
        // 2. 生成随机验证码
        String code = RandomUtil.randomNumbers(6);
        
        // 3. 构建短信参数
        Map<String, Object> param = new HashMap<>();
        param.put("code", code);
        
        // 4. 发送短信
        boolean sendResult = sendSms(phoneNumber, businessType, param);
        
        if (sendResult) {
            // 发送成功，更新计数器
            redisTemplate.opsForValue().increment(dailyCountKey);
            // 设置过期时间（如果key不存在）
            redisTemplate.expire(dailyCountKey, RedisConstants.SMS_CODE_DAILY_COUNT_EXPIRE_SECONDS, TimeUnit.SECONDS);
            
            // 验证码存入Redis
            String codeKey = RedisConstants.SMS_CODE_PREFIX + businessType + ":" + phoneNumber;
            redisTemplate.opsForValue().set(codeKey, code, RedisConstants.SMS_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            
            // 保存验证码记录到数据库
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expirationTime = now.plusMinutes(RedisConstants.SMS_CODE_EXPIRE_MINUTES);
            
            VerificationCodeRecord record = new VerificationCodeRecord()
                    .setPhoneNumber(phoneNumber)
                    .setVerificationCode(code)
                    .setBusinessType(businessType)
                    .setSendTime(now)
                    .setExpirationTime(expirationTime)
                    .setIsUsed(false)
                    .setAttemptCount(0);
            
            verificationCodeRecordMapper.insert(record);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * 验证验证码
     *
     * @param smsVerifyDTO 短信验证DTO
     * @return 验证结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean verifyCode(SmsVerifyDTO smsVerifyDTO) {
        String phoneNumber = smsVerifyDTO.getPhoneNumber();
        String code = smsVerifyDTO.getCode();
        String businessType = smsVerifyDTO.getBusinessType();
        
        // 1. 从Redis获取验证码
        String codeKey = RedisConstants.SMS_CODE_PREFIX + businessType + ":" + phoneNumber;
        String cachedCode = redisTemplate.opsForValue().get(codeKey);
        
        // 2. 验证码不存在或已过期
        if (cachedCode == null) {
            log.warn("验证码已过期或不存在，手机号：{}", phoneNumber);
            return false;
        }
        
        // 3. 验证码不匹配
        if (!cachedCode.equals(code)) {
            log.warn("验证码不匹配，手机号：{}，输入：{}，实际：{}", phoneNumber, code, cachedCode);
            
            // 增加验证码记录的尝试次数
            LambdaQueryWrapper<VerificationCodeRecord> queryWrapper = new LambdaQueryWrapper<VerificationCodeRecord>()
                    .eq(VerificationCodeRecord::getPhoneNumber, phoneNumber)
                    .eq(VerificationCodeRecord::getBusinessType, businessType)
                    .eq(VerificationCodeRecord::getIsUsed, false)
                    .orderByDesc(VerificationCodeRecord::getSendTime)
                    .last("LIMIT 1");
            
            VerificationCodeRecord record = verificationCodeRecordMapper.selectOne(queryWrapper);
            if (record != null) {
                record.setAttemptCount(record.getAttemptCount() + 1);
                verificationCodeRecordMapper.updateById(record);
                
                // 如果尝试次数过多，让验证码失效
                if (record.getAttemptCount() >= 5) {
                    invalidateCode(phoneNumber, businessType);
                    log.warn("验证码尝试次数过多，已失效，手机号：{}", phoneNumber);
                }
            }
            
            return false;
        }
        
        // 4. 验证成功，使验证码失效
        invalidateCode(phoneNumber, businessType);
        
        // 5. 更新数据库记录状态
        LambdaQueryWrapper<VerificationCodeRecord> queryWrapper = new LambdaQueryWrapper<VerificationCodeRecord>()
                .eq(VerificationCodeRecord::getPhoneNumber, phoneNumber)
                .eq(VerificationCodeRecord::getBusinessType, businessType)
                .eq(VerificationCodeRecord::getIsUsed, false)
                .orderByDesc(VerificationCodeRecord::getSendTime)
                .last("LIMIT 1");
        
        VerificationCodeRecord record = verificationCodeRecordMapper.selectOne(queryWrapper);
        if (record != null) {
            record.setIsUsed(true);
            verificationCodeRecordMapper.updateById(record);
        }
        
        return true;
    }
    
    /**
     * 使验证码失效
     *
     * @param phoneNumber 手机号
     * @param businessType 业务类型
     */
    @Override
    public void invalidateCode(String phoneNumber, String businessType) {
        String codeKey = RedisConstants.SMS_CODE_PREFIX + businessType + ":" + phoneNumber;
        redisTemplate.delete(codeKey);
    }
    
    /**
     * 发送短信
     *
     * @param phoneNumber 手机号
     * @param businessType 业务类型
     * @param param 短信参数
     * @return 发送结果
     */
    private boolean sendSms(String phoneNumber, String businessType, Map<String, Object> param) {
        // 配置阿里云区域和AccessKey
        DefaultProfile profile = DefaultProfile.getProfile(
                "default", 
                smsConfig.getAccessKeyId(), 
                smsConfig.getAccessKeySecret()
        );
        
        IAcsClient client = new DefaultAcsClient(profile);
        
        // 创建请求
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        
        // 设置请求参数
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("SignName", smsConfig.getSignName());
        
        // 根据业务类型设置模板
        String templateCode = "";
        switch (businessType) {
            case RedisConstants.BUSINESS_TYPE_REGISTER:
                templateCode = smsConfig.getTemplateCode().getRegister();
                break;
            case RedisConstants.BUSINESS_TYPE_FORGET_PASSWORD:
                templateCode = smsConfig.getTemplateCode().getForgetPassword();
                break;
            case RedisConstants.BUSINESS_TYPE_PAYMENT_PASSWORD:
                templateCode = smsConfig.getTemplateCode().getPaymentPassword();
                break;
            default:
                log.error("未知的业务类型：{}", businessType);
                return false;
        }
        
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));
        
        try {
            // 发送短信
            CommonResponse response = client.getCommonResponse(request);
            log.info("短信发送响应：{}", response.getData());
            
            // 解析响应
            JSONObject jsonObject = JSONObject.parseObject(response.getData());
            return "OK".equals(jsonObject.getString("Code"));
        } catch (Exception e) {
            log.error("短信发送异常", e);
            return false;
        }
    }
} 