package com.xuchao.ershou.service;

import com.xuchao.ershou.model.entity.UserAddress;

public interface UserAddressService {
    /**
     * 添加用户地址
     * @param userAddress 用户地址信息
     * @return 是否添加成功
     */
    boolean addUserAddress(UserAddress userAddress);
}