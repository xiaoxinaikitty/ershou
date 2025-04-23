package com.xuchao.ershou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuchao.ershou.mapper.WalletAccountMapper;
import com.xuchao.ershou.model.dto.CreateWalletDTO;
import com.xuchao.ershou.model.dto.FreezeWalletDTO;
import com.xuchao.ershou.model.dto.UpdateWalletDTO;
import com.xuchao.ershou.model.entity.WalletAccount;
import com.xuchao.ershou.service.WalletAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包账户Service实现类
 */
@Service
public class WalletAccountServiceImpl extends ServiceImpl<WalletAccountMapper, WalletAccount> implements WalletAccountService {

    /**
     * 创建钱包账户
     *
     * @param createWalletDTO 创建钱包参数
     * @return 钱包账户实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WalletAccount createWalletAccount(CreateWalletDTO createWalletDTO) {
        // 检查该用户是否已有钱包账户
        WalletAccount existAccount = getWalletAccountByUserId(createWalletDTO.getUserId());
        if (existAccount != null) {
            throw new RuntimeException("该用户已有钱包账户，无需重复创建");
        }

        // 创建新钱包账户
        WalletAccount walletAccount = new WalletAccount();
        walletAccount.setUserId(createWalletDTO.getUserId());
        walletAccount.setBalance(BigDecimal.ZERO);
        walletAccount.setFrozenBalance(BigDecimal.ZERO);

        // 保存到数据库
        this.save(walletAccount);
        return walletAccount;
    }

    /**
     * 根据用户ID查询钱包账户
     *
     * @param userId 用户ID
     * @return 钱包账户实体
     */
    @Override
    public WalletAccount getWalletAccountByUserId(Long userId) {
        LambdaQueryWrapper<WalletAccount> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WalletAccount::getUserId, userId);
        return this.getOne(queryWrapper);
    }
    
    /**
     * 更新钱包账户余额
     *
     * @param updateWalletDTO 更新钱包参数
     * @return 更新后的钱包账户实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WalletAccount updateWalletAccount(UpdateWalletDTO updateWalletDTO) {
        // 查询钱包账户是否存在
        WalletAccount walletAccount = this.getById(updateWalletDTO.getAccountId());
        if (walletAccount == null) {
            throw new RuntimeException("钱包账户不存在");
        }
        
        // 更新余额和冻结余额
        if (updateWalletDTO.getBalance() != null) {
            walletAccount.setBalance(updateWalletDTO.getBalance());
        }
        
        if (updateWalletDTO.getFrozenBalance() != null) {
            walletAccount.setFrozenBalance(updateWalletDTO.getFrozenBalance());
        }
        
        // 更新最后更新时间
        walletAccount.setLastUpdateTime(LocalDateTime.now());
        
        // 更新到数据库
        this.updateById(walletAccount);
        
        return walletAccount;
    }
    
    /**
     * 冻结或解冻钱包账户余额
     *
     * @param freezeWalletDTO 冻结/解冻钱包参数
     * @return 更新后的钱包账户实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WalletAccount freezeOrUnfreezeBalance(FreezeWalletDTO freezeWalletDTO) {
        // 查询钱包账户是否存在
        WalletAccount walletAccount = this.getById(freezeWalletDTO.getAccountId());
        if (walletAccount == null) {
            throw new RuntimeException("钱包账户不存在");
        }
        
        // 获取操作金额，确保为正数
        BigDecimal amount = freezeWalletDTO.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("操作金额必须大于0");
        }
        
        // 根据操作类型处理
        Integer operationType = freezeWalletDTO.getOperationType();
        
        if (operationType == 1) {
            // 冻结操作
            // 检查可用余额是否足够
            if (walletAccount.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("可用余额不足，无法冻结");
            }
            
            // 更新余额和冻结余额
            walletAccount.setBalance(walletAccount.getBalance().subtract(amount));
            walletAccount.setFrozenBalance(walletAccount.getFrozenBalance().add(amount));
        } else if (operationType == 2) {
            // 解冻操作
            // 检查冻结余额是否足够
            if (walletAccount.getFrozenBalance().compareTo(amount) < 0) {
                throw new RuntimeException("冻结余额不足，无法解冻");
            }
            
            // 更新余额和冻结余额
            walletAccount.setBalance(walletAccount.getBalance().add(amount));
            walletAccount.setFrozenBalance(walletAccount.getFrozenBalance().subtract(amount));
        } else {
            throw new RuntimeException("无效的操作类型，1表示冻结，2表示解冻");
        }
        
        // 更新最后更新时间
        walletAccount.setLastUpdateTime(LocalDateTime.now());
        
        // 更新到数据库
        this.updateById(walletAccount);
        
        return walletAccount;
    }
    
    /**
     * 根据账户ID查询钱包账户
     *
     * @param accountId 钱包账户ID
     * @return 钱包账户实体
     */
    @Override
    public WalletAccount getWalletAccountById(Long accountId) {
        return this.getById(accountId);
    }
} 