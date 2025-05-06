package com.xuchao.ershou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuchao.ershou.mapper.UserAddressMapper;
import com.xuchao.ershou.model.dto.UserAddressAddRequest;
import com.xuchao.ershou.model.entity.UserAddress;
import com.xuchao.ershou.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public boolean addUserAddress(UserAddress userAddress) {
        int rows = userAddressMapper.insert(userAddress);
        return rows > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addAddressByUserId(Long userId, UserAddressAddRequest addressRequest) {
        // 如果设置为默认地址，需要将其他地址改为非默认
        if (Boolean.TRUE.equals(addressRequest.getIsDefault())) {
            // 创建一个只更新isDefault字段的对象
            UserAddress defaultToFalse = new UserAddress();
            defaultToFalse.setIsDefault(false);
            
            // 创建更新条件：用户ID相同的所有地址
            LambdaQueryWrapper<UserAddress> updateWrapper = new LambdaQueryWrapper<>();
            updateWrapper.eq(UserAddress::getUserId, userId);
            
            // 更新所有当前用户的地址为非默认
            userAddressMapper.update(defaultToFalse, updateWrapper);
        }
        
        // 将DTO转换为实体对象
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        userAddress.setConsignee(addressRequest.getConsignee());
        userAddress.setRegion(addressRequest.getRegion());
        userAddress.setDetail(addressRequest.getDetail());
        userAddress.setContactPhone(addressRequest.getContactPhone());
        userAddress.setIsDefault(addressRequest.getIsDefault());
        
        // 插入地址并获取主键ID
        userAddressMapper.insert(userAddress);
        return userAddress.getAddressId();
    }
    
    @Override
    public List<UserAddress> getUserAddresses(Long userId) {
        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getCreateTime);
        return userAddressMapper.selectList(queryWrapper);
    }
    
    @Override
    public boolean deleteUserAddress(Long userId, Long addressId) {
        // 创建删除条件：用户ID和地址ID都匹配
        LambdaQueryWrapper<UserAddress> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getAddressId, addressId);
        
        // 执行删除操作
        int rows = userAddressMapper.delete(deleteWrapper);
        
        return rows > 0;
    }
}