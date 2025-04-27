package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.mapper.UserMapper;
import com.xuchao.ershou.model.dao.user.UserAdminDao;
import com.xuchao.ershou.model.dao.user.UserBanDao;
import com.xuchao.ershou.model.dao.user.UserChangePasswordDao;
import com.xuchao.ershou.model.dao.user.UserRoleUpdateDao;
import com.xuchao.ershou.model.dao.user.UserUnbanDao;
import com.xuchao.ershou.model.dao.user.UserUpdateDao;
import com.xuchao.ershou.model.dao.user.UserResetPasswordDao;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.model.dao.user.UserLoginDao;
import com.xuchao.ershou.model.dao.user.UserAddressDao;
import com.xuchao.ershou.model.entity.UserAddress;
import com.xuchao.ershou.model.vo.UserRoleVO;
import com.xuchao.ershou.service.UserService;
import com.xuchao.ershou.service.SmsService;
import com.xuchao.ershou.model.dto.SmsVerifyDTO;
import com.xuchao.ershou.common.constant.RedisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xuchao.ershou.common.ErrorCode;
import org.springframework.util.StringUtils;
import com.xuchao.ershou.model.dao.user.UserPageQueryDao;
import com.xuchao.ershou.model.vo.PageResult;
import com.xuchao.ershou.model.vo.UserPageVO;
import org.springframework.beans.BeanUtils;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import java.util.Objects;
import java.time.ZoneId;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SmsService smsService;

    // 系统管理员角色常量
    private static final String ADMIN_ROLE = "系统管理员";
    // 普通用户角色常量
    private static final String NORMAL_ROLE = "普通用户";

    @Override
    public int insertUser(User user) {
        return userMapper.insertUser(user);
    }

    @Override
    public int deleteUserById(Long userId) {
        return userMapper.deleteById(userId);
    }

    @Override
    public int updateUser(User user) {
        return userMapper.updateById(user);
    }

    @Override
    public User selectUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return userMapper.countByUsername(username) > 0;
    }

    @Override
    public User selectUserByUsernameAndPassword(UserLoginDao loginDao) {
        return userMapper.selectUserByUsernameAndPassword(
            loginDao.getUsername(), 
            loginDao.getPassword()
        );
    }

    @Override
    public User selectAdminByUsernameAndPassword(UserAdminDao adminDao) {
        return userMapper.selectAdminByUsernameAndPassword(
            adminDao.getUsername(),
            adminDao.getPassword()
        );
    }

    @Override
    public int insertUserAddress(UserAddress userAddress) {
        return userMapper.insertUserAddress(userAddress);
    }
    
    @Override
    public User getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            // 清除敏感字段
            user.setPassword(null);
        }
        return user;
    }
    
    @Override
    public User updateUserInfo(Long userId, UserUpdateDao updateDao) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        
        // 更新用户信息
        boolean hasUpdates = false;
        
        if (StringUtils.hasText(updateDao.getPhone())) {
            user.setPhone(updateDao.getPhone());
            hasUpdates = true;
        }
        
        if (StringUtils.hasText(updateDao.getEmail())) {
            user.setEmail(updateDao.getEmail());
            hasUpdates = true;
        }
        
        if (StringUtils.hasText(updateDao.getAvatar())) {
            user.setAvatar(updateDao.getAvatar());
            hasUpdates = true;
        }
        
        if (StringUtils.hasText(updateDao.getPassword())) {
            user.setPassword(updateDao.getPassword());
            hasUpdates = true;
        }
        
        // 如果有更新，则保存到数据库
        if (hasUpdates) {
            int result = userMapper.updateById(user);
            if (result <= 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户信息失败");
            }
        }
        
        // 返回更新后的用户信息（去除敏感字段）
        user.setPassword(null);
        return user;
    }
    
    @Override
    public boolean changePassword(Long userId, UserChangePasswordDao passwordDao) {
        // 1. 参数校验
        if (!Objects.equals(passwordDao.getNewPassword(), passwordDao.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的新密码不一致");
        }
        
        // 2. 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        
        // 3. 校验旧密码是否正确
        if (!Objects.equals(user.getPassword(), passwordDao.getOldPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "旧密码不正确");
        }
        
        // 4. 更新密码
        user.setPassword(passwordDao.getNewPassword());
        int result = userMapper.updateById(user);
        
        return result > 0;
    }

    @Override
    public boolean updateUserRole(Long currentUserId, UserRoleUpdateDao roleUpdateDao) {
        // 1. 检查当前用户是否是管理员
        User currentUser = userMapper.selectById(currentUserId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "当前用户不存在");
        }
        
        // 仅允许系统管理员修改用户角色
        if (!ADMIN_ROLE.equals(currentUser.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "权限不足，仅系统管理员可执行此操作");
        }
        
        // 2. 检查目标用户是否存在
        Long targetUserId = roleUpdateDao.getTargetUserId();
        User targetUser = userMapper.selectById(targetUserId);
        if (targetUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "目标用户不存在");
        }
        
        // 3. 设置用户角色（根据请求设置角色，若未指定则默认设为系统管理员）
        String newRole = roleUpdateDao.getRole();
        if (!StringUtils.hasText(newRole)) {
            newRole = ADMIN_ROLE;  // 默认设为系统管理员
        } else {
            // 检查角色值是否合法
            if (!ADMIN_ROLE.equals(newRole) && !NORMAL_ROLE.equals(newRole)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的角色值，角色只能是'系统管理员'或'普通用户'");
            }
        }
        
        // 如果当前已经是想要设置的角色，不执行更新操作
        if (newRole.equals(targetUser.getRole())) {
            return true;  // 认为设置成功
        }
        
        // 4. 更新用户角色
        targetUser.setRole(newRole);
        int result = userMapper.updateById(targetUser);
        
        return result > 0;
    }

    @Override
    public UserRoleVO getUserRole(Long userId) {
        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        
        // 组装用户角色信息
        UserRoleVO userRoleVO = new UserRoleVO();
        userRoleVO.setUserId(user.getUserId());
        userRoleVO.setUsername(user.getUsername());
        userRoleVO.setRole(user.getRole());
        
        // 判断是否是管理员
        boolean isAdmin = ADMIN_ROLE.equals(user.getRole());
        userRoleVO.setIsAdmin(isAdmin);
        
        return userRoleVO;
    }

    @Override
    public boolean banUser(Long currentUserId, UserBanDao userBanDao) {
        // 1. 检查当前用户是否是管理员
        User currentUser = userMapper.selectById(currentUserId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "当前用户不存在");
        }
        
        // 仅允许系统管理员进行封禁操作
        if (!ADMIN_ROLE.equals(currentUser.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "权限不足，仅系统管理员可执行此操作");
        }
        
        // 2. 检查目标用户是否存在
        Long targetUserId = userBanDao.getTargetUserId();
        User targetUser = userMapper.selectById(targetUserId);
        if (targetUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "目标用户不存在");
        }
        
        // 3. 不允许封禁管理员用户
        if (ADMIN_ROLE.equals(targetUser.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "不允许封禁管理员账户");
        }
        
        // 4. 如果用户已被封禁，直接返回成功
        if (targetUser.getIsLocked() != null && targetUser.getIsLocked()) {
            return true;
        }
        
        // 5. 更新用户状态为封禁
        targetUser.setIsLocked(true);
        
        // 6. 设置封禁原因
        String banReason = userBanDao.getBanReason();
        if (!StringUtils.hasText(banReason)) {
            banReason = "违反用户协议";
        }
        
        // 将封禁原因保存到用户对象中
        targetUser.setBanReason(banReason);
        
        // 记录日志
        System.out.println("用户" + targetUser.getUsername() + "被封禁，封禁时间：" + LocalDateTime.now() + ", 原因：" + banReason);
        
        // 7. 执行更新操作
        int result = userMapper.updateById(targetUser);
        
        return result > 0;
    }

    @Override
    public boolean unbanUser(Long currentUserId, UserUnbanDao userUnbanDao) {
        // 1. 检查当前用户是否是管理员
        User currentUser = userMapper.selectById(currentUserId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "当前用户不存在");
        }
        
        // 仅允许系统管理员进行解封操作
        if (!ADMIN_ROLE.equals(currentUser.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "权限不足，仅系统管理员可执行此操作");
        }
        
        // 2. 检查目标用户是否存在
        Long targetUserId = userUnbanDao.getTargetUserId();
        User targetUser = userMapper.selectById(targetUserId);
        if (targetUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "目标用户不存在");
        }
        
        // 3. 检查用户是否处于封禁状态
        if (targetUser.getIsLocked() == null || !targetUser.getIsLocked()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该用户当前未被封禁");
        }
        
        // 记录解封操作的日志
        String unbanReason = userUnbanDao.getUnbanReason();
        if (!StringUtils.hasText(unbanReason)) {
            unbanReason = "管理员手动操作";
        }
        
        // 记录解封日志（实际项目中可以存入数据库的操作日志表）
        System.out.println("用户" + targetUser.getUsername() + "被解封，解封时间：" 
            + LocalDateTime.now() + ", 原因：" + unbanReason + ", 操作管理员: " + currentUser.getUsername());
        
        // 4. 使用UpdateWrapper明确指定要更新的字段，确保将ban_reason设置为null
        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<User> updateWrapper = 
            new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        updateWrapper.eq("user_id", targetUserId)
                     .set("is_locked", false)
                     .set("ban_reason", null);
        
        // 5. 执行更新操作
        int result = userMapper.update(null, updateWrapper);
        
        return result > 0;
    }

    @Override
    public boolean resetPassword(UserResetPasswordDao resetPasswordDao) {
        // 1. 参数校验
        if (!Objects.equals(resetPasswordDao.getNewPassword(), resetPasswordDao.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的新密码不一致");
        }
        
        // 2. 验证验证码
        SmsVerifyDTO smsVerifyDTO = new SmsVerifyDTO();
        smsVerifyDTO.setPhoneNumber(resetPasswordDao.getPhoneNumber());
        smsVerifyDTO.setCode(resetPasswordDao.getCode());
        smsVerifyDTO.setBusinessType(RedisConstants.BUSINESS_TYPE_FORGET_PASSWORD);
        
        boolean verifyResult = smsService.verifyCode(smsVerifyDTO);
        if (!verifyResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误或已过期");
        }
        
        // 3. 根据手机号查找用户
        User user = userMapper.getUserByPhone(resetPasswordDao.getPhoneNumber());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "未找到绑定该手机号的用户");
        }
        
        // 4. 更新密码
        user.setPassword(resetPasswordDao.getNewPassword());
        int result = userMapper.updateById(user);
        
        return result > 0;
    }

    @Override
    public PageResult<UserPageVO> pageUsers(UserPageQueryDao queryParams) {
        // 参数默认值处理
        if (queryParams.getPageNum() == null || queryParams.getPageNum() < 1) {
            queryParams.setPageNum(1);
        }
        if (queryParams.getPageSize() == null || queryParams.getPageSize() < 1) {
            queryParams.setPageSize(10);
        }
        // 限制每页最大条数
        if (queryParams.getPageSize() > 50) {
            queryParams.setPageSize(50);
        }
        
        // 查询总记录数
        Long total = userMapper.countUsers(queryParams);
        
        // 如果没有记录，直接返回空列表
        if (total == 0) {
            return new PageResult<>(queryParams.getPageNum(), queryParams.getPageSize(), 0, new ArrayList<>());
        }
        
        // 查询用户列表
        List<User> userList = userMapper.listUsers(queryParams);
        List<UserPageVO> userPageVOList = new ArrayList<>();
        
        // 转换为VO对象
        for (User user : userList) {
            UserPageVO userPageVO = new UserPageVO();
            BeanUtils.copyProperties(user, userPageVO);
            
            // 设置状态文本
            boolean isLocked = user.getIsLocked() != null && user.getIsLocked();
            userPageVO.setStatusText(isLocked ? "禁用" : "正常");
            userPageVO.setStatus(isLocked ? 0 : 1);
            
            // 敏感信息脱敏
            if (StringUtils.hasText(user.getPhone())) {
                userPageVO.setPhone(maskPhoneNumber(user.getPhone()));
            }
            if (StringUtils.hasText(user.getEmail())) {
                userPageVO.setEmail(maskEmail(user.getEmail()));
            }
            
            // 设置注册时间
            if (user.getCreateTime() != null) {
                Date registerTime = Date.from(user.getCreateTime().atZone(ZoneId.systemDefault()).toInstant());
                userPageVO.setRegisterTime(registerTime);
            } else {
                userPageVO.setRegisterTime(null);
            }
            
            // 最后登录时间暂不可用
            userPageVO.setLastLoginTime(null);
            
            // TODO: 查询用户发布的商品数量
            userPageVO.setProductCount(0);
            
            // TODO: 查询用户的订单数量
            userPageVO.setOrderCount(0);
            
            userPageVOList.add(userPageVO);
        }
        
        // 构造分页结果并返回
        return new PageResult<>(queryParams.getPageNum(), queryParams.getPageSize(), total, userPageVOList);
    }
    
    @Override
    public Long countAllUsers() {
        return userMapper.countAllUsers();
    }
    
    @Override
    public Long countUsersByLockState(boolean isLocked) {
        return userMapper.countUsersByLockState(isLocked);
    }
    
    @Override
    public Long countUsersByRole(String role) {
        return userMapper.countUsersByRole(role);
    }
    
    /**
     * 手机号脱敏
     */
    private String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
    
    /**
     * 邮箱脱敏
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 2) {
            return email;
        }
        return email.substring(0, 2) + "****" + email.substring(atIndex);
    }
}