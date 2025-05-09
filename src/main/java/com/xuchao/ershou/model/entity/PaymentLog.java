package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("payment_log")
public class PaymentLog {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer paymentId;
    
    private String orderNo;
    
    private String alipayNo;
    
    private String eventType;
    
    private String content;
    
    private Date createTime;
} 