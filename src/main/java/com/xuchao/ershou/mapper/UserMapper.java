package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.model.entity.UserAddress;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends BaseMapper<User> {
    int insertUser(User user);
    
    int countByUsername(String username);
    
    User selectUserByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    User selectAdminByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    
    int insertUserAddress(UserAddress userAddress);
    
    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(@Param("userId") Long userId);
    
    /**
     * 根据手机号查询用户信息
     * @param phone 手机号
     * @return 用户信息
     */
    User getUserByPhone(@Param("phone") String phone);
}