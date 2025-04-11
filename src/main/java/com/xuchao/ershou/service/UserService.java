package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dao.user.UserAddressDao;
import com.xuchao.ershou.model.dao.user.UserAdminDao;
import com.xuchao.ershou.model.dao.user.UserLoginDao;
import com.xuchao.ershou.model.dao.user.UserUpdateDao;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.model.entity.UserAddress;

public interface UserService {
    int insertUser(User user);
    int deleteUserById(Long userId);
    int updateUser(User user);
    User selectUserById(Long userId);
    
    boolean checkUsernameExists(String username);

    User selectUserByUsernameAndPassword(UserLoginDao loginDao);
    User selectAdminByUsernameAndPassword(UserAdminDao adminDao);
    
    int insertUserAddress(UserAddress userAddress);
    
    // 获取用户详细信息（非敏感字段）
    User getUserInfo(Long userId);
    
    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param updateDao 更新的用户信息
     * @return 更新后的用户信息
     */
    User updateUserInfo(Long userId, UserUpdateDao updateDao);
}