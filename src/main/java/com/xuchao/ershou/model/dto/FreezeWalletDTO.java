package com.xuchao.ershou.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * 冻结/解冻钱包账户DTO
 */
@Data
public class FreezeWalletDTO {

    /**
     * 钱包账户ID
     */
    @NotNull(message = "账户ID不能为空")
    private Long accountId;

    /**
     * 操作类型（1-冻结，2-解冻）
     */
    @NotNull(message = "操作类型不能为空")
    private Integer operationType;

    /**
     * 要冻结或解冻的金额
     */
    @NotNull(message = "金额不能为空")
    @Min(value = 0, message = "金额必须大于等于0")
    private BigDecimal amount;
} 