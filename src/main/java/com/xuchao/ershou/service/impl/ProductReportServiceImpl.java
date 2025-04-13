package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.mapper.ProductReportMapper;
import com.xuchao.ershou.mapper.ProductMapper;
import com.xuchao.ershou.model.dao.product.ProductReportAddDao;
import com.xuchao.ershou.model.entity.Product;
import com.xuchao.ershou.model.entity.ProductReport;
import com.xuchao.ershou.model.vo.ProductReportVO;
import com.xuchao.ershou.service.ProductReportService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品举报服务实现类
 */
@Service
public class ProductReportServiceImpl implements ProductReportService {

    @Autowired
    private ProductReportMapper productReportMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    @Transactional
    public ProductReportVO addProductReport(Long userId, ProductReportAddDao productReportAddDao) {
        // 参数校验
        if (productReportAddDao == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "举报信息不能为空");
        }
        
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        Long productId = productReportAddDao.getProductId();
        
        // 检查商品是否存在
        Product product = productMapper.selectProductById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        
        // 校验举报类型合法性
        Integer reportType = productReportAddDao.getReportType();
        if (reportType == null || reportType < 1 || reportType > 3) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "举报类型无效");
        }
        
        // 检查是否已举报
        int existCount = productReportMapper.checkUserReport(userId, productId);
        if (existCount > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您已举报过该商品，请勿重复举报");
        }
        
        // 创建举报记录
        ProductReport productReport = new ProductReport();
        productReport.setProductId(productId);
        productReport.setUserId(userId);
        productReport.setReportType(reportType);
        productReport.setReportContent(productReportAddDao.getReportContent());
        productReport.setStatus(0); // 默认状态：0-未处理
        
        // 插入数据库
        int result = productReportMapper.insertProductReport(productReport);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "举报提交失败");
        }
        
        // 构建返回数据
        ProductReportVO reportVO = new ProductReportVO();
        BeanUtils.copyProperties(productReport, reportVO);
        
        // 填充商品标题
        reportVO.setProductTitle(product.getTitle());
        
        return reportVO;
    }
}
