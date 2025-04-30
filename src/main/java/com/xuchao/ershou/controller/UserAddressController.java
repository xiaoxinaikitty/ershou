package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.entity.UserAddress;
import com.xuchao.ershou.service.UserAddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}