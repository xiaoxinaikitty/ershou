package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.mapper.UserMapper;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}