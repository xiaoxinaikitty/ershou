package com.xuchao.ershou.model.dao.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 取消订单数据传输对象
 */
@Data
public class OrderCancelDao {
    
    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    /**
     * 取消原因
     */
    private String remark;
}