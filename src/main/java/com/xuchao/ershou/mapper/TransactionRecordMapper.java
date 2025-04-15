package com.xuchao.ershou.mapper;

import com.xuchao.ershou.model.entity.TransactionRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionRecordMapper {
    /**
     * 插入交易记录
     * @param record 交易记录
     * @return 影响行数
     */
    int insertTransactionRecord(TransactionRecord record);
} 