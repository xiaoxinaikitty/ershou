package com.xuchao.ershou.model.dao.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户信息修改DTO
 */
@Data
public class UserUpdateDao {
    
    // 用户名，不允许修改，仅用于显示
    private String username;
    
    // 手机号
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    // 电子邮箱
    @Email(message = "邮箱格式不正确")
    private String email;
    
    // 头像路径
    private String avatar;
    
    // 密码（如需修改密码）
    private String password;
}