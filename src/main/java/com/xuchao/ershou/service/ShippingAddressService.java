package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dto.ShippingAddressAddRequest;
import com.xuchao.ershou.model.entity.ShippingAddress;

import java.util.List;

public interface ShippingAddressService {
    /**
     * 添加发货地址
     * @param shippingAddress 发货地址信息
     * @return 是否添加成功
     */
    boolean addShippingAddress(ShippingAddress shippingAddress);
    
    /**
     * 根据用户ID添加发货地址
     * @param userId 用户ID
     * @param addressRequest 地址信息请求
     * @return 添加的地址ID
     */
    Long addShippingAddressByUserId(Long userId, ShippingAddressAddRequest addressRequest);
    
    /**
     * 获取用户的所有发货地址
     * @param userId 用户ID
     * @return 地址列表
     */
    List<ShippingAddress> getUserShippingAddresses(Long userId);
    
    /**
     * 删除用户发货地址
     * @param userId 用户ID
     * @param addressId 地址ID
     * @return 是否删除成功
     */
    boolean deleteShippingAddress(Long userId, Long addressId);
} 