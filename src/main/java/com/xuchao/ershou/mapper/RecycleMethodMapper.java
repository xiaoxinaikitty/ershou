package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.RecycleMethod;
import org.apache.ibatis.annotations.Param;

/**
 * 商品交易方式Mapper接口
 */
public interface RecycleMethodMapper extends BaseMapper<RecycleMethod> {
    
    /**
     * 检查方式名称是否已存在
     * @param methodName 方式名称
     * @return 存在的数量
     */
    int checkMethodNameExists(@Param("methodName") String methodName);
    
    /**
     * 插入交易方式
     * @param recycleMethod 交易方式信息
     * @return 影响的行数
     */
    int insertRecycleMethod(RecycleMethod recycleMethod);
}