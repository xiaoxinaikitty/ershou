package com.xuchao.ershou.model.dao.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 封禁用户DTO
 */
@Data
public class UserBanDao {
    
    /**
     * 需要封禁的用户ID
     */
    @NotNull(message = "目标用户ID不能为空")
    private Long targetUserId;
    
    /**
     * 封禁原因
     */
    private String banReason;
}