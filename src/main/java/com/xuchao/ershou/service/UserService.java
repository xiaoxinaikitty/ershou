package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dao.user.UserLoginDao;
import com.xuchao.ershou.model.entity.User;

public interface UserService {
    int insertUser(User user);
    int deleteUserById(Long userId);
    int updateUser(User user);
    User selectUserById(Long userId);
    
    boolean checkUsernameExists(String username);

    User selectUserByUsernameAndPassword(UserLoginDao loginDao);
}