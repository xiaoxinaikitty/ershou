package com.xuchao.ershou.model.dto;

import lombok.Data;

/**
 * 支付宝支付DTO类
 */
@Data
public class AliPayDTO {
    /**
     * 商户订单号，商户网站订单系统中唯一订单号
     */
    private String traceNo;
    
    /**
     * 订单总金额，单位为元，精确到小数点后两位
     */
    private String totalAmount;
    
    /**
     * 订单标题
     */
    private String subject;
    
    /**
     * 商品描述
     */
    private String body;
} 