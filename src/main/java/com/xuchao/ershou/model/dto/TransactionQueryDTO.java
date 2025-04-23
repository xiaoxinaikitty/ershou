package com.xuchao.ershou.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 查询钱包交易记录列表DTO
 */
@Data
public class TransactionQueryDTO {

    /**
     * 钱包账户ID
     */
    @NotNull(message = "账户ID不能为空")
    private Long accountId;

    /**
     * 查询起始时间
     */
    private LocalDateTime startTime;

    /**
     * 查询结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 交易类型(可选，1充值 2消费 3退款 4其他)
     */
    private Integer transactionType;
    
    /**
     * 当前页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页记录数
     */
    private Integer pageSize = 10;
} 