package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends BaseMapper<User> {
    int insertUser(User user);
    
    int countByUsername(String username);
    
    User selectUserByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    User selectAdminByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
}