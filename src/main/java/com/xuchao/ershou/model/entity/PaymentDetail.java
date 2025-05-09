package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("payment_detail")
public class PaymentDetail {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer paymentId;
    
    private Integer orderId;
    
    private Integer productId;
    
    private String productName;
    
    private Integer quantity;
    
    private BigDecimal price;
    
    private BigDecimal subTotal;
} 