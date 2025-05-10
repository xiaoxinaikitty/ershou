package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 支付记录Mapper接口
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
    
    /**
     * 更新支付状态
     * @param traceNo 订单号
     * @param state 状态
     * @param payTime 支付时间
     * @return 影响行数
     */
    @Update("UPDATE payment SET state = #{state}, pay_time = #{payTime}, alipay_no = #{alipayNo} WHERE no = #{traceNo}")
    int updateState(@Param("traceNo") String traceNo, @Param("state") String state, 
                    @Param("payTime") String payTime, @Param("alipayNo") String alipayNo);
} 