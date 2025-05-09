package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dto.AliPayDTO;
import com.xuchao.ershou.model.entity.Payment;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface PaymentService {
    
    /**
     * 创建支付宝支付
     *
     * @param aliPayDTO 支付请求参数
     * @return 支付表单HTML字符串
     */
    String pay(AliPayDTO aliPayDTO);
    
    /**
     * 处理支付宝支付回调
     *
     * @param request HTTP请求
     * @return 处理结果
     */
    String handlePayNotify(HttpServletRequest request);
    
    /**
     * 保存支付记录
     *
     * @param payment 支付记录
     * @return 是否保存成功
     */
    boolean savePayment(Payment payment);
    
    /**
     * 根据订单号查询支付记录
     *
     * @param orderNo 订单号
     * @return 支付记录
     */
    Payment getPaymentByOrderNo(String orderNo);
    
    /**
     * 创建支付日志
     *
     * @param paymentId 支付ID
     * @param orderNo 订单号
     * @param alipayNo 支付宝交易号
     * @param eventType 事件类型
     * @param content 日志内容
     */
    void createPaymentLog(Integer paymentId, String orderNo, String alipayNo, String eventType, String content);
} 