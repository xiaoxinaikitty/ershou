package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.VerificationCodeRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 验证码记录Mapper接口
 */
@Mapper
public interface VerificationCodeRecordMapper extends BaseMapper<VerificationCodeRecord> {
} 