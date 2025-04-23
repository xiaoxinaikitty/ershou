package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.mapper.UserMapper;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.service.VerificationCodeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 * 注：实际环境中，应该接入短信服务或邮件服务发送真实验证码
 * 这里使用内存Map存储模拟验证码发送和验证过程
 * 注意：这是一个临时实现，生产环境应该使用Redis替代内存存储
 */
@Service
@Slf4j
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Resource
    private UserMapper userMapper;
    
    // 验证码有效期（分钟）
    private static final int CODE_EXPIRE_MINUTES = 5;
    
    // 使用内存Map存储验证码（临时方案，生产环境建议使用Redis）
    private static final Map<String, String> CODE_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Long> EXPIRE_MAP = new ConcurrentHashMap<>();
    
    // 用于清理过期验证码的定时任务
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);
    
    static {
        // 每分钟检查一次过期的验证码并清理
        SCHEDULER.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            EXPIRE_MAP.entrySet().removeIf(entry -> {
                if (entry.getValue() < now) {
                    CODE_MAP.remove(entry.getKey());
                    return true;
                }
                return false;
            });
        }, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * 发送验证码
     * 注：实际环境中，应该调用短信服务或邮件服务发送真实验证码
     *
     * @param userId 用户ID
     * @param type 验证码类型
     * @return 是否发送成功
     */
    @Override
    public boolean sendVerificationCode(Long userId, String type) {
        // 参数校验
        if (userId == null || type == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        
        // 获取联系方式（手机或邮箱）
        String contactInfo = user.getPhone();
        if (contactInfo == null || contactInfo.isEmpty()) {
            contactInfo = user.getEmail();
            if (contactInfo == null || contactInfo.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户未设置手机号或邮箱，无法发送验证码");
            }
        }
        
        // 生成6位随机验证码
        String code = generateCode(6);
        
        // 构建key
        String codeKey = buildCodeKey(userId, type);
        
        // 将验证码存入Map，设置过期时间
        CODE_MAP.put(codeKey, code);
        EXPIRE_MAP.put(codeKey, System.currentTimeMillis() + CODE_EXPIRE_MINUTES * 60 * 1000);
        
        // 在实际应用中，这里应该调用短信服务或邮件服务发送验证码
        // 这里仅打印日志模拟发送验证码
        log.info("向用户 {} 发送验证码：{}，类型：{}，联系方式：{}", userId, code, type, contactInfo);
        
        return true;
    }

    /**
     * 验证验证码是否正确
     *
     * @param userId 用户ID
     * @param code 验证码
     * @param type 验证码类型
     * @return 是否验证通过
     */
    @Override
    public boolean verifyCode(Long userId, String code, String type) {
        // 参数校验
        if (userId == null || code == null || type == null) {
            return false;
        }
        
        // 构建key
        String codeKey = buildCodeKey(userId, type);
        
        // 从Map获取存储的验证码
        String storedCode = CODE_MAP.get(codeKey);
        
        // 检查是否过期
        Long expireTime = EXPIRE_MAP.get(codeKey);
        if (storedCode == null || expireTime == null || expireTime < System.currentTimeMillis()) {
            // 已过期，清理
            CODE_MAP.remove(codeKey);
            EXPIRE_MAP.remove(codeKey);
            return false;
        }
        
        // 验证码匹配（忽略大小写）
        boolean isMatch = storedCode.equalsIgnoreCase(code);
        
        // 如果验证通过，删除Map中的验证码（一次性使用）
        if (isMatch) {
            CODE_MAP.remove(codeKey);
            EXPIRE_MAP.remove(codeKey);
        }
        
        return isMatch;
    }
    
    /**
     * 生成指定长度的随机数字验证码
     */
    private String generateCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    /**
     * 构建存储验证码的key
     */
    private String buildCodeKey(Long userId, String type) {
        return "verification_code:" + type + ":" + userId;
    }
} 