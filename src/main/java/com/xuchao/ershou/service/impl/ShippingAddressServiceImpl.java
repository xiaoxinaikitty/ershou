package com.xuchao.ershou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuchao.ershou.mapper.ShippingAddressMapper;
import com.xuchao.ershou.model.dto.ShippingAddressAddRequest;
import com.xuchao.ershou.model.entity.ShippingAddress;
import com.xuchao.ershou.service.ShippingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShippingAddressServiceImpl implements ShippingAddressService {

    @Autowired
    private ShippingAddressMapper shippingAddressMapper;

    @Override
    public boolean addShippingAddress(ShippingAddress shippingAddress) {
        int rows = shippingAddressMapper.insert(shippingAddress);
        return rows > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addShippingAddressByUserId(Long userId, ShippingAddressAddRequest addressRequest) {
        // 如果设置为默认地址，需要将其他地址改为非默认
        if (Boolean.TRUE.equals(addressRequest.getIsDefault())) {
            // 创建一个只更新isDefault字段的对象
            ShippingAddress defaultToFalse = new ShippingAddress();
            defaultToFalse.setIsDefault(false);
            
            // 创建更新条件：用户ID相同的所有地址
            LambdaQueryWrapper<ShippingAddress> updateWrapper = new LambdaQueryWrapper<>();
            updateWrapper.eq(ShippingAddress::getUserId, userId);
            
            // 更新所有当前用户的地址为非默认
            shippingAddressMapper.update(defaultToFalse, updateWrapper);
        }
        
        // 将DTO转换为实体对象
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setUserId(userId);
        shippingAddress.setShipperName(addressRequest.getShipperName());
        shippingAddress.setRegion(addressRequest.getRegion());
        shippingAddress.setDetail(addressRequest.getDetail());
        shippingAddress.setContactPhone(addressRequest.getContactPhone());
        shippingAddress.setIsDefault(addressRequest.getIsDefault());
        
        // 插入地址并获取主键ID
        shippingAddressMapper.insert(shippingAddress);
        return shippingAddress.getAddressId();
    }
    
    @Override
    public List<ShippingAddress> getUserShippingAddresses(Long userId) {
        LambdaQueryWrapper<ShippingAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShippingAddress::getUserId, userId)
                .orderByDesc(ShippingAddress::getIsDefault)
                .orderByDesc(ShippingAddress::getCreateTime);
        return shippingAddressMapper.selectList(queryWrapper);
    }
    
    @Override
    public boolean deleteShippingAddress(Long userId, Long addressId) {
        // 查询地址是否存在且属于当前用户
        ShippingAddress address = shippingAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            return false;
        }
        
        // 删除地址
        return shippingAddressMapper.deleteById(addressId) > 0;
    }
} 