package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dao.product.ProductReportAddDao;
import com.xuchao.ershou.model.vo.ProductReportVO;

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
}
