package com.xuchao.ershou.model.dao.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户修改密码DTO
 */
@Data
public class UserChangePasswordDao {
    
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;
    
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String newPassword;
    
    @NotBlank(message = "确认新密码不能为空")
    private String confirmPassword;
}