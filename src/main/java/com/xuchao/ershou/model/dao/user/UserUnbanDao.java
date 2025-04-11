package com.xuchao.ershou.model.dao.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 解封用户DTO
 */
@Data
public class UserUnbanDao {
    
    /**
     * 需要解封的用户ID
     */
    @NotNull(message = "目标用户ID不能为空")
    private Long targetUserId;
    
    /**
     * 解封原因（可选）
     */
    private String unbanReason;
}