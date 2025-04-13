package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dao.product.ProductReportAddDao;
import com.xuchao.ershou.model.vo.ProductReportVO;

import java.util.List;

/**
 * 商品举报服务接口
 */
public interface ProductReportService {
    
    /**
     * 添加商品举报
     * @param userId 当前登录用户ID
     * @param productReportAddDao 举报商品信息
     * @return 举报结果
     */
    ProductReportVO addProductReport(Long userId, ProductReportAddDao productReportAddDao);
    
    /**
     * 查询指定商品的举报信息列表
     * @param productId 商品ID
     * @return 举报信息列表
     */
    List<ProductReportVO> getProductReports(Long productId);
}
