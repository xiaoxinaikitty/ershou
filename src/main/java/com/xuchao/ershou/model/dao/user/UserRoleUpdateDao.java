package com.xuchao.ershou.model.dao.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户角色修改DTO
 */
@Data
public class UserRoleUpdateDao {
    
    /**
     * 目标用户ID
     */
    @NotNull(message = "目标用户ID不能为空")
    private Long targetUserId;
    
    /**
     * 角色名称（系统管理员/普通用户）
     */
    private String role;
}