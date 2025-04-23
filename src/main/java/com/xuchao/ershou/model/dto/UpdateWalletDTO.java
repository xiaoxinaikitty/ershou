package com.xuchao.ershou.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 更新钱包账户DTO
 */
@Data
public class UpdateWalletDTO {

    /**
     * 钱包账户ID
     */
    @NotNull(message = "账户ID不能为空")
    private Long accountId;

    /**
     * 更新后的余额
     */
    private BigDecimal balance;

    /**
     * 更新后的冻结余额
     */
    private BigDecimal frozenBalance;
} 