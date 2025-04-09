package com.xuchao.ershou.service;

import com.xuchao.ershou.model.entity.User;

public interface UserService {
    int insertUser(User user);
    int deleteUserById(Long userId);
    int updateUser(User user);
    User selectUserById(Long userId);
    
    boolean checkUsernameExists(String username);
}