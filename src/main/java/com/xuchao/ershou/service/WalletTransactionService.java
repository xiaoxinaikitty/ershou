package com.xuchao.ershou.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xuchao.ershou.model.dto.CreateTransactionDTO;
import com.xuchao.ershou.model.dto.TransactionQueryDTO;
import com.xuchao.ershou.model.entity.WalletTransaction;

import java.util.List;

/**
 * 钱包交易记录Service接口
 */
public interface WalletTransactionService extends IService<WalletTransaction> {

    /**
     * 创建钱包交易记录
     *
     * @param createTransactionDTO 创建交易记录参数
     * @return 钱包交易记录实体
     */
    WalletTransaction createTransaction(CreateTransactionDTO createTransactionDTO);
    
    /**
     * 分页查询钱包交易记录列表
     * 
     * @param queryDTO 查询参数
     * @return 交易记录分页结果
     */
    IPage<WalletTransaction> queryTransactionList(TransactionQueryDTO queryDTO);
} 