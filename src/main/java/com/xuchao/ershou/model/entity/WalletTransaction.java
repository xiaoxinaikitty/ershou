package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包交易记录实体类
 */
@Data
@TableName("wallet_transaction")
public class WalletTransaction {

    /**
     * 交易ID（唯一主键）
     */
    @TableId(value = "transaction_id", type = IdType.AUTO)
    private Long transactionId;

    /**
     * 钱包账户ID，关联wallet_account表的account_id
     */
    private Long accountId;

    /**
     * 交易类型(1充值 2消费 3退款 4其他)
     */
    private Integer transactionType;

    /**
     * 交易金额
     */
    private BigDecimal transactionAmount;

    /**
     * 交易前余额
     */
    private BigDecimal beforeBalance;

    /**
     * 交易后余额
     */
    private BigDecimal afterBalance;

    /**
     * 交易时间
     */
    private LocalDateTime transactionTime;

    /**
     * 交易备注
     */
    private String remark;
} 