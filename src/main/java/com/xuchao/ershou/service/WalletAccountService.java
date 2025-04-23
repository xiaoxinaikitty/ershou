package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dto.CreateWalletDTO;
import com.xuchao.ershou.model.dto.FreezeWalletDTO;
import com.xuchao.ershou.model.dto.UpdateWalletDTO;
import com.xuchao.ershou.model.entity.WalletAccount;

/**
 * 钱包账户Service接口
 */
public interface WalletAccountService {

    /**
     * 创建钱包账户
     *
     * @param createWalletDTO 创建钱包参数
     * @return 钱包账户实体
     */
    WalletAccount createWalletAccount(CreateWalletDTO createWalletDTO);
    
    /**
     * 根据用户ID查询钱包账户
     *
     * @param userId 用户ID
     * @return 钱包账户实体
     */
    WalletAccount getWalletAccountByUserId(Long userId);
    
    /**
     * 更新钱包账户余额
     *
     * @param updateWalletDTO 更新钱包参数
     * @return 更新后的钱包账户实体
     */
    WalletAccount updateWalletAccount(UpdateWalletDTO updateWalletDTO);
    
    /**
     * 冻结或解冻钱包账户余额
     *
     * @param freezeWalletDTO 冻结/解冻钱包参数
     * @return 更新后的钱包账户实体
     */
    WalletAccount freezeOrUnfreezeBalance(FreezeWalletDTO freezeWalletDTO);
    
    /**
     * 根据账户ID查询钱包账户
     *
     * @param accountId 钱包账户ID
     * @return 钱包账户实体
     */
    WalletAccount getWalletAccountById(Long accountId);
} 