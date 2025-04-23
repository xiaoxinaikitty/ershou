package com.xuchao.ershou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuchao.ershou.mapper.WalletTransactionMapper;
import com.xuchao.ershou.model.dto.CreateTransactionDTO;
import com.xuchao.ershou.model.dto.TransactionQueryDTO;
import com.xuchao.ershou.model.entity.WalletAccount;
import com.xuchao.ershou.model.entity.WalletTransaction;
import com.xuchao.ershou.service.WalletAccountService;
import com.xuchao.ershou.service.WalletTransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 钱包交易记录Service实现类
 */
@Service
public class WalletTransactionServiceImpl extends ServiceImpl<WalletTransactionMapper, WalletTransaction> implements WalletTransactionService {

    @Resource
    private WalletAccountService walletAccountService;

    /**
     * 创建钱包交易记录
     *
     * @param createTransactionDTO 创建交易记录参数
     * @return 钱包交易记录实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WalletTransaction createTransaction(CreateTransactionDTO createTransactionDTO) {
        // 验证钱包账户是否存在
        WalletAccount walletAccount = walletAccountService.getWalletAccountById(createTransactionDTO.getAccountId());
        if (walletAccount == null) {
            throw new RuntimeException("钱包账户不存在");
        }
        
        // 验证交易类型是否有效
        Integer transactionType = createTransactionDTO.getTransactionType();
        if (transactionType < 1 || transactionType > 4) {
            throw new RuntimeException("无效的交易类型，有效范围：1-充值 2-消费 3-退款 4-其他");
        }
        
        // 创建交易记录
        WalletTransaction transaction = new WalletTransaction();
        transaction.setAccountId(createTransactionDTO.getAccountId());
        transaction.setTransactionType(transactionType);
        transaction.setTransactionAmount(createTransactionDTO.getTransactionAmount());
        transaction.setBeforeBalance(createTransactionDTO.getBeforeBalance());
        transaction.setAfterBalance(createTransactionDTO.getAfterBalance());
        transaction.setRemark(createTransactionDTO.getRemark());
        transaction.setTransactionTime(LocalDateTime.now());
        
        // 保存交易记录
        this.save(transaction);
        
        return transaction;
    }
    
    /**
     * 分页查询钱包交易记录列表
     * 
     * @param queryDTO 查询参数
     * @return 交易记录分页结果
     */
    @Override
    public IPage<WalletTransaction> queryTransactionList(TransactionQueryDTO queryDTO) {
        // 验证钱包账户是否存在
        WalletAccount walletAccount = walletAccountService.getWalletAccountById(queryDTO.getAccountId());
        if (walletAccount == null) {
            throw new RuntimeException("钱包账户不存在");
        }
        
        // 构建查询条件
        LambdaQueryWrapper<WalletTransaction> queryWrapper = new LambdaQueryWrapper<>();
        
        // 按账户ID查询
        queryWrapper.eq(WalletTransaction::getAccountId, queryDTO.getAccountId());
        
        // 按交易类型查询（如果指定了）
        if (queryDTO.getTransactionType() != null) {
            queryWrapper.eq(WalletTransaction::getTransactionType, queryDTO.getTransactionType());
        }
        
        // 按交易时间范围查询
        if (queryDTO.getStartTime() != null) {
            queryWrapper.ge(WalletTransaction::getTransactionTime, queryDTO.getStartTime());
        }
        
        if (queryDTO.getEndTime() != null) {
            queryWrapper.le(WalletTransaction::getTransactionTime, queryDTO.getEndTime());
        }
        
        // 按交易时间降序排序（最新的交易记录在前）
        queryWrapper.orderByDesc(WalletTransaction::getTransactionTime);
        
        // 构建分页对象
        Page<WalletTransaction> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 执行分页查询
        return this.page(page, queryWrapper);
    }
} 