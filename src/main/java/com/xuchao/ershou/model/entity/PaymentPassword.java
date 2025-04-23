package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支付密码实体类
 */
@Data
@TableName("payment_password")
public class PaymentPassword {

    /**
     * 支付密码记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long passwordId;

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 加密后的支付密码
     */
    private String hashedPaymentPassword;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;
} 