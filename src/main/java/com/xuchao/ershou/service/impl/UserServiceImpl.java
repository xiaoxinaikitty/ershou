package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.mapper.UserMapper;
import com.xuchao.ershou.model.dao.user.UserAdminDao;
import com.xuchao.ershou.model.dao.user.UserChangePasswordDao;
import com.xuchao.ershou.model.dao.user.UserUpdateDao;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.model.dao.user.UserLoginDao;
import com.xuchao.ershou.model.dao.user.UserAddressDao;
import com.xuchao.ershou.model.entity.UserAddress;
import com.xuchao.ershou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xuchao.ershou.common.ErrorCode;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

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
}