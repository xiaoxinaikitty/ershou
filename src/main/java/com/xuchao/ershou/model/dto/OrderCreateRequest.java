package com.xuchao.ershou.model.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.xuchao.ershou.model.dao.order.OrderCreateDao;
import com.xuchao.ershou.model.dao.order.OrderAddressDao;

import java.math.BigDecimal;

/**
 * 订单创建请求DTO
 * 用于适配前端发送的参数格式
 */
@Data
@Slf4j
public class OrderCreateRequest {
    
    /**
     * 商品ID
     */
    private Integer productId;
    
    /**
     * 地址ID
     */
    private Integer addressId;
    
    /**
     * 卖家ID
     */
    private Integer sellerId;
    
    /**
     * 支付方式(1在线支付 2线下交易)
     */
    private Integer paymentType;
    
    /**
     * 配送方式(1自提 2快递)
     */
    private Integer deliveryType;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 商品单价
     */
    private Double price;
    
    /**
     * 运费
     */
    private Double deliveryFee;
    
    /**
     * 转换为后端OrderCreateDao对象
     * @param userId 用户ID
     * @param userAddress 用户地址对象
     * @return OrderCreateDao
     */
    public OrderCreateDao toOrderCreateDao(Long userId, com.xuchao.ershou.model.entity.UserAddress userAddress) {
        OrderCreateDao orderCreateDao = new OrderCreateDao();
        
        // 基本参数
        orderCreateDao.setProductId(productId != null ? productId.longValue() : null);
        orderCreateDao.setSellerId(sellerId != null ? sellerId.longValue() : null);
        orderCreateDao.setPaymentType(paymentType);
        orderCreateDao.setDeliveryType(deliveryType);
        orderCreateDao.setRemark(remark);
        
        // 金额计算
        BigDecimal productPrice = price != null ? new BigDecimal(String.valueOf(price)) : BigDecimal.ZERO;
        BigDecimal shippingFee = deliveryFee != null ? new BigDecimal(String.valueOf(deliveryFee)) : BigDecimal.ZERO;
        BigDecimal totalAmount = productPrice.add(shippingFee);
        
        orderCreateDao.setOrderAmount(totalAmount);
        orderCreateDao.setPaymentAmount(totalAmount);
        orderCreateDao.setDeliveryFee(shippingFee);
        
        // 设置地址信息
        OrderAddressDao addressDao = new OrderAddressDao();
        
        if (userAddress != null) {
            // 设置收货人信息
            addressDao.setReceiverName(userAddress.getConsignee());
            addressDao.setReceiverPhone(userAddress.getContactPhone());
            
            // 处理地区信息
            String[] regions = userAddress.getRegion().split(" ");
            addressDao.setProvince(regions.length > 0 ? regions[0] : "");
            addressDao.setCity(regions.length > 1 ? regions[1] : "");
            addressDao.setDistrict(regions.length > 2 ? regions[2] : "");
            
            // 设置详细地址
            addressDao.setDetailAddress(userAddress.getDetail());
        } else {
            // 在线支付可能没有地址，创建一个空地址对象
            if (paymentType != null && paymentType == 1) {
                // 支付宝支付 - 设置默认值
                addressDao.setReceiverName("在线支付");
                addressDao.setReceiverPhone("13800000000");
                addressDao.setProvince("在线支付");
                addressDao.setCity("在线支付");
                addressDao.setDistrict("在线支付");
                addressDao.setDetailAddress("在线支付无需地址");
            }
        }
        
        orderCreateDao.setAddress(addressDao);
        
        return orderCreateDao;
    }
} 