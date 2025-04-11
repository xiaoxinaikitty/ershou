package com.xuchao.ershou.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户角色信息视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleVO {
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户角色（系统管理员/普通用户）
     */
    private String role;
    
    /**
     * 是否为管理员
     */
    private Boolean isAdmin;
}