package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dto.ShippingAddressAddRequest;
import com.xuchao.ershou.model.entity.ShippingAddress;
import com.xuchao.ershou.service.ShippingAddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 发货地址控制器
 */
@RestController
@RequestMapping("/shipping/address")
public class ShippingAddressController {

    @Autowired
    private ShippingAddressService shippingAddressService;

    /**
     * 添加发货地址
     * @param addressRequest 发货地址信息请求
     * @return 添加的地址ID
     */
    @PostMapping("/add")
    public BaseResponse<Map<String, Long>> addShippingAddress(@RequestBody @Valid ShippingAddressAddRequest addressRequest) {
        // 获取当前登录用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 调用服务层添加地址
        Long addressId = shippingAddressService.addShippingAddressByUserId(currentUserId, addressRequest);
        
        // 返回结果中包含新增地址的ID
        return ResultUtils.success(Map.of("addressId", addressId));
    }
    
    /**
     * 获取当前用户的所有发货地址
     * @return 地址列表
     */
    @GetMapping("/list")
    public BaseResponse<List<ShippingAddress>> getShippingAddresses() {
        // 获取当前登录用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 获取用户地址列表
        List<ShippingAddress> addresses = shippingAddressService.getUserShippingAddresses(currentUserId);
        return ResultUtils.success(addresses);
    }
    
    /**
     * 删除用户发货地址
     * @param addressId 地址ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{addressId}")
    public BaseResponse<String> deleteShippingAddress(@PathVariable Long addressId) {
        // 获取当前登录用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 调用服务层删除地址
        boolean result = shippingAddressService.deleteShippingAddress(currentUserId, addressId);
        if (result) {
            return ResultUtils.success("发货地址删除成功");
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "发货地址删除失败，可能地址不存在或无权限删除");
        }
    }
} 