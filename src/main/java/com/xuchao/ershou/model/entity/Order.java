package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单主表实体类
 */
@Data
@TableName("`order`")  // order是关键字，需要用反引号
@Accessors(chain = true)
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "order_id", type = IdType.AUTO)
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
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 配送方式(1自提 2快递)
     */
    private Integer deliveryType;

    /**
     * 运费
     */
    private BigDecimal deliveryFee;

    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 收货时间
     */
    private LocalDateTime receivedTime;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}