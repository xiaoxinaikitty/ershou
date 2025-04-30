package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.mapper.UserAddressMapper;
import com.xuchao.ershou.model.entity.UserAddress;
import com.xuchao.ershou.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public boolean addUserAddress(UserAddress userAddress) {
        int rows = userAddressMapper.insert(userAddress);
        return rows > 0;
    }
}