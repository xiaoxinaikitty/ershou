package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dto.AliPayDTO;
import com.xuchao.ershou.model.entity.Payment;

import java.util.Map;

/**
 * 支付服务接口
 */
public interface PaymentService {
    
    /**
     * 创建支付记录
     * @param payment 支付记录
     * @return 创建结果
     */
    boolean create(Payment payment);
    
    /**
     * 支付宝支付
     * @param aliPayDTO 支付信息
     * @return 支付表单HTML
     */
    String pay(AliPayDTO aliPayDTO);
    
    /**
     * 获取支付宝支付URL
     * @param aliPayDTO 支付信息
     * @return 支付URL
     */
    String getPayUrl(AliPayDTO aliPayDTO);
    
    /**
     * 处理支付宝回调
     * @param params 回调参数
     * @return 处理结果
     */
    String handleNotify(Map<String, String> params);
} 