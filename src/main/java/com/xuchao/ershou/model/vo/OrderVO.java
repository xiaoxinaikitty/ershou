package com.xuchao.ershou.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单响应视图对象
 */
@Data
public class OrderVO {
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 订单编号
     */
    private String orderNo;
    
    /**
     * 买家用户ID
     */
    private Long userId;
    
    /**
     * 卖家用户ID
     */
    private Long sellerId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品标题
     */
    private String productTitle;
    
    /**
     * 订单金额
     */
    private BigDecimal orderAmount;
    
    /**
     * 实付金额
     */
    private BigDecimal paymentAmount;
    
    /**
     * 订单状态(0待付款 1待发货 2待收货 3已完成 4已取消 5退款中 6已退款)
     */
    private Integer orderStatus;
    
    /**
     * 支付方式(1在线支付 2线下交易)
     */
    private Integer paymentType;
    
    /**
     * 配送方式(1自提 2快递)
     */
    private Integer deliveryType;
    
    /**
     * 运费
     */
    private BigDecimal deliveryFee;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 收货地址
     */
    private OrderAddressVO address;
}