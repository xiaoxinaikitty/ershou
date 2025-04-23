package com.xuchao.ershou.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * 创建钱包交易记录DTO
 */
@Data
public class CreateTransactionDTO {

    /**
     * 钱包账户ID
     */
    @NotNull(message = "账户ID不能为空")
    private Long accountId;

    /**
     * 交易类型(1充值 2消费 3退款 4其他)
     */
    @NotNull(message = "交易类型不能为空")
    private Integer transactionType;

    /**
     * 交易金额
     */
    @NotNull(message = "交易金额不能为空")
    @Min(value = 0, message = "交易金额必须大于等于0")
    private BigDecimal transactionAmount;

    /**
     * 交易前余额
     */
    @NotNull(message = "交易前余额不能为空")
    private BigDecimal beforeBalance;

    /**
     * 交易后余额
     */
    @NotNull(message = "交易后余额不能为空")
    private BigDecimal afterBalance;

    /**
     * 交易备注
     */
    private String remark;
} 