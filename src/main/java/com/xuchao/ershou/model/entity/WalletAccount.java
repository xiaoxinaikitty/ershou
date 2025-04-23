package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包账户实体类
 */
@Data
@TableName("wallet_account")
public class WalletAccount {

    /**
     * 钱包账户ID（唯一主键）
     */
    @TableId(value = "account_id", type = IdType.AUTO)
    private Long accountId;

    /**
     * 所属用户ID，关联user表的user_id
     */
    private Long userId;

    /**
     * 钱包余额
     */
    private BigDecimal balance;

    /**
     * 冻结余额，如在交易处理中被冻结的金额
     */
    private BigDecimal frozenBalance;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;
} 