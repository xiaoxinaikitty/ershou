package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.ProductReport;
import org.apache.ibatis.annotations.Param;

/**
 * 商品举报Mapper接口
 */
public interface ProductReportMapper extends BaseMapper<ProductReport> {
    
    /**
     * 检查用户是否已举报商品
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 举报记录数
     */
    int checkUserReport(@Param("userId") Long userId, @Param("productId") Long productId);
    
    /**
     * 插入商品举报信息
     * @param productReport 商品举报信息
     * @return 影响的行数
     */
    int insertProductReport(ProductReport productReport);
}
