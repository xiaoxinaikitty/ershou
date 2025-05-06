package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dto.UserAddressAddRequest;
import com.xuchao.ershou.model.entity.UserAddress;
import com.xuchao.ershou.service.UserAddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户地址控制器
 */
@RestController
@RequestMapping("/user/address")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    /**
     * 添加收货地址
     * @param userAddress 收货地址信息
     * @return 添加结果
     */
    @PostMapping("/add")
    public BaseResponse<String> addAddress(@RequestBody @Valid UserAddress userAddress) {
        // 获取当前登录用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }

        // 设置用户ID到地址对象
        userAddress.setUserId(currentUserId);

        // 调用服务层添加地址
        boolean result = userAddressService.addUserAddress(userAddress);
        if (result) {
            return ResultUtils.success("地址添加成功");
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "地址添加失败");
        }
    }
    
    /**
     * 根据用户ID添加收货地址
     * @param addressRequest 收货地址信息请求
     * @return 添加的地址ID
     */
    @PostMapping("/addByUser")
    public BaseResponse<Map<String, Long>> addAddressByUser(@RequestBody @Valid UserAddressAddRequest addressRequest) {
        // 获取当前登录用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 调用服务层添加地址
        Long addressId = userAddressService.addAddressByUserId(currentUserId, addressRequest);
        
        // 返回结果中包含新增地址的ID
        return ResultUtils.success(Map.of("addressId", addressId));
    }
    
    /**
     * 获取当前用户的所有收货地址
     * @return 地址列表
     */
    @GetMapping("/list")
    public BaseResponse<List<UserAddress>> getUserAddresses() {
        // 获取当前登录用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 获取用户地址列表
        List<UserAddress> addresses = userAddressService.getUserAddresses(currentUserId);
        return ResultUtils.success(addresses);
    }
    
    /**
     * 删除用户收货地址
     * @param addressId 地址ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{addressId}")
    public BaseResponse<String> deleteUserAddress(@PathVariable Long addressId) {
        // 获取当前登录用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 调用服务层删除地址
        boolean result = userAddressService.deleteUserAddress(currentUserId, addressId);
        if (result) {
            return ResultUtils.success("收货地址删除成功");
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "收货地址删除失败，可能地址不存在或无权限删除");
        }
    }
}