package com.xuchao.ershou.model.dao.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建订单的数据传输对象
 */
@Data
public class OrderCreateDao {
    
    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    /**
     * 卖家ID
     */
    @NotNull(message = "卖家ID不能为空")
    private Long sellerId;
    
    /**
     * 支付方式(1在线支付 2线下交易)
     */
    @NotNull(message = "支付方式不能为空")
    @Min(value = 1, message = "支付方式无效")
    @Max(value = 2, message = "支付方式无效")
    private Integer paymentType;
    
    /**
     * 配送方式(1自提 2快递)
     */
    @NotNull(message = "配送方式不能为空")
    @Min(value = 1, message = "配送方式无效")
    @Max(value = 2, message = "配送方式无效")
    private Integer deliveryType;
    
    /**
     * 订单金额
     */
    @NotNull(message = "订单金额不能为空")
    @DecimalMin(value = "0.01", message = "订单金额必须大于0")
    private BigDecimal orderAmount;
    
    /**
     * 实付金额
     */
    @NotNull(message = "实付金额不能为空")
    @DecimalMin(value = "0.01", message = "实付金额必须大于0")
    private BigDecimal paymentAmount;
    
    /**
     * 运费
     */
    @DecimalMin(value = "0", message = "运费不能为负数")
    private BigDecimal deliveryFee = BigDecimal.ZERO;
    
    /**
     * 订单备注
     */
    @Size(max = 255, message = "订单备注最多255个字符")
    private String remark;
    
    /**
     * 收货地址信息
     */
    @Valid
    @NotNull(message = "收货地址不能为空")
    private OrderAddressDao address;
}