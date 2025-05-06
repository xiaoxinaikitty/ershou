package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dto.UserAddressAddRequest;
import com.xuchao.ershou.model.entity.UserAddress;

import java.util.List;

public interface UserAddressService {
    /**
     * 添加用户地址
     * @param userAddress 用户地址信息
     * @return 是否添加成功
     */
    boolean addUserAddress(UserAddress userAddress);
    
    /**
     * 根据用户ID添加收货地址
     * @param userId 用户ID
     * @param addressRequest 地址信息请求
     * @return 添加的地址ID
     */
    Long addAddressByUserId(Long userId, UserAddressAddRequest addressRequest);
    
    /**
     * 获取用户的所有收货地址
     * @param userId 用户ID
     * @return 地址列表
     */
    List<UserAddress> getUserAddresses(Long userId);
    
    /**
     * 删除用户收货地址
     * @param userId 用户ID
     * @param addressId 地址ID
     * @return 是否删除成功
     */
    boolean deleteUserAddress(Long userId, Long addressId);
}