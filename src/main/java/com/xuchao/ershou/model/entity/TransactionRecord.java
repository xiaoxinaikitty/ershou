package com.xuchao.ershou.model.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionRecord {
    private Long transactionId;
    private Long orderId;
    private Long userId;
    private Integer transactionType;
    private String transactionNo;
    private BigDecimal amount;
    private Integer status;
    private Integer paymentChannel;
    private String remark;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
} 