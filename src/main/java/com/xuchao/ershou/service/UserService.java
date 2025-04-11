package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dao.user.UserAddressDao;
import com.xuchao.ershou.model.dao.user.UserAdminDao;
import com.xuchao.ershou.model.dao.user.UserChangePasswordDao;
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
    
    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param passwordDao 密码修改信息(旧密码、新密码)
     * @return 是否修改成功
     */
    boolean changePassword(Long userId, UserChangePasswordDao passwordDao);
}