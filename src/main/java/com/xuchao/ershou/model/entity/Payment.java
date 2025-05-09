package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("payment")
public class Payment {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer userId;
    
    private String alipayNo;
    
    private String name;
    
    private String no;
    
    private BigDecimal totalPrice;
    
    private String state;
    
    private Date createTime;
    
    private Date payTime;
} 