package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.model.dto.CreateWalletDTO;
import com.xuchao.ershou.model.dto.FreezeWalletDTO;
import com.xuchao.ershou.model.dto.UpdateWalletDTO;
import com.xuchao.ershou.model.entity.WalletAccount;
import com.xuchao.ershou.service.WalletAccountService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 钱包账户接口
 */
@RestController
@RequestMapping("/api/wallet")
public class WalletAccountController {

    @Resource
    private WalletAccountService walletAccountService;

    /**
     * 创建钱包账户
     *
     * @param createWalletDTO 创建钱包参数
     * @return 账户ID
     */
    @PostMapping("/create")
    public BaseResponse<Long> createWalletAccount(@RequestBody CreateWalletDTO createWalletDTO) {
        if (createWalletDTO == null || createWalletDTO.getUserId() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        
        try {
            // 调用服务创建钱包账户
            WalletAccount walletAccount = walletAccountService.createWalletAccount(createWalletDTO);
            return ResultUtils.success(walletAccount.getAccountId());
        } catch (Exception e) {
            // 异常处理
            return ResultUtils.error(ErrorCode.OPERATION_ERROR.getCode(), e.getMessage());
        }
    }
    
    /**
     * 查询钱包账户信息
     *
     * @param userId 用户ID
     * @return 钱包账户信息
     */
    @GetMapping("/info")
    public BaseResponse<WalletAccount> getWalletAccountInfo(@RequestParam("userId") Long userId) {
        if (userId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        
        try {
            // 调用服务查询钱包账户
            WalletAccount walletAccount = walletAccountService.getWalletAccountByUserId(userId);
            if (walletAccount == null) {
                return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
            }
            return ResultUtils.success(walletAccount);
        } catch (Exception e) {
            // 异常处理
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR.getCode(), e.getMessage());
        }
    }
    
    /**
     * 更新钱包账户余额
     *
     * @param updateWalletDTO 更新钱包参数
     * @return 更新后的钱包账户信息
     */
    @PutMapping("/update")
    public BaseResponse<WalletAccount> updateWalletAccount(@RequestBody UpdateWalletDTO updateWalletDTO) {
        if (updateWalletDTO == null || updateWalletDTO.getAccountId() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        
        try {
            // 调用服务更新钱包账户
            WalletAccount updatedWallet = walletAccountService.updateWalletAccount(updateWalletDTO);
            return ResultUtils.success(updatedWallet);
        } catch (Exception e) {
            // 异常处理
            return ResultUtils.error(ErrorCode.OPERATION_ERROR.getCode(), e.getMessage());
        }
    }
    
    /**
     * 冻结/解冻钱包账户余额
     *
     * @param freezeWalletDTO 冻结/解冻钱包参数
     * @return 更新后的钱包账户信息
     */
    @PutMapping("/freeze")
    public BaseResponse<WalletAccount> freezeOrUnfreezeBalance(@RequestBody FreezeWalletDTO freezeWalletDTO) {
        if (freezeWalletDTO == null || freezeWalletDTO.getAccountId() == null 
                || freezeWalletDTO.getOperationType() == null || freezeWalletDTO.getAmount() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        
        // 验证操作类型
        Integer operationType = freezeWalletDTO.getOperationType();
        if (operationType != 1 && operationType != 2) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "操作类型必须为1(冻结)或2(解冻)");
        }
        
        try {
            // 调用服务冻结/解冻钱包账户
            WalletAccount updatedWallet = walletAccountService.freezeOrUnfreezeBalance(freezeWalletDTO);
            return ResultUtils.success(updatedWallet);
        } catch (Exception e) {
            // 异常处理
            return ResultUtils.error(ErrorCode.OPERATION_ERROR.getCode(), e.getMessage());
        }
    }
} 