package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付记录实体类
 */
@Data
@TableName("payment")
public class Payment {
    /**
     * 支付记录ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 支付宝交易号
     */
    private String alipayNo;
    
    /**
     * 支付名称
     */
    private String name;
    
    /**
     * 内部订单号
     */
    private String no;
    
    /**
     * 支付总金额
     */
    private BigDecimal totalPrice;
    
    /**
     * 支付状态：收款/未收款/等待支付
     */
    private String state;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 支付时间
     */
    private Date payTime;
} 