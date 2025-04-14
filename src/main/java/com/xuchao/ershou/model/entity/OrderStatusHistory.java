package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单状态变更记录实体类
 */
@Data
@TableName("order_status_history")
@Accessors(chain = true)
public class OrderStatusHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 前一状态
     */
    private Integer previousStatus;

    /**
     * 当前状态
     */
    private Integer currentStatus;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人类型(1买家 2卖家 3系统 4管理员)
     */
    private Integer operatorType;

    /**
     * 变更备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}