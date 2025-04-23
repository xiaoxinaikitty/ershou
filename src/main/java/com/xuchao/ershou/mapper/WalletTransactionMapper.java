package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.WalletTransaction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 钱包交易记录Mapper接口
 */
@Mapper
public interface WalletTransactionMapper extends BaseMapper<WalletTransaction> {
} 