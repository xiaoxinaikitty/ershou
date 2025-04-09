package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.User;

public interface UserMapper extends BaseMapper<User> {
    int insertUser(User user);
    
    int countByUsername(String username);
}