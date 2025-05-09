package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
    
    /**
     * 更新支付状态
     *
     * @param no 订单号
     * @param state 支付状态
     * @param payTime 支付时间
     * @return 更新行数
     */
    @Update("UPDATE payment SET state = #{state}, alipay_no = #{alipayNo}, pay_time = #{payTime} WHERE no = #{no}")
    int updateState(@Param("no") String no, @Param("state") String state, 
                   @Param("alipayNo") String alipayNo, @Param("payTime") Date payTime);
} 