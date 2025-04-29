package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.ProductReport;
import com.xuchao.ershou.model.vo.ProductReportVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
    
    /**
     * 查询指定商品的举报信息列表
     * @param productId 商品ID
     * @return 举报信息列表
     */
    List<ProductReportVO> selectReportsByProductId(@Param("productId") Long productId);

    /**
     * 查询举报商品总数
     * @param status 处理状态
     * @param reportType 举报类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 总记录数
     */
    long countProductReports(@Param("status") Integer status, @Param("reportType") Integer reportType, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 分页查询举报商品列表
     * @param status 处理状态
     * @param reportType 举报类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 举报商品列表
     */
    List<ProductReportVO> selectProductReports(@Param("status") Integer status, @Param("reportType") Integer reportType, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("offset") int offset, @Param("limit") int limit);
}
