package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.mapper.ProductMapper;
import com.xuchao.ershou.model.dao.product.ProductAddDao;
import com.xuchao.ershou.model.entity.Product;
import com.xuchao.ershou.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xuchao.ershou.common.ErrorCode;

import java.time.LocalDateTime;

/**
 * 商品服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    
    @Override
    public Product addProduct(Long userId, ProductAddDao productAddDao) {
        // 参数校验（虽然有注解校验，这里再做一层基本校验）
        if (productAddDao == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品信息不能为空");
        }
        
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 创建商品对象并设置属性
        Product product = new Product();
        BeanUtils.copyProperties(productAddDao, product);
        
        // 设置商品发布者ID
        product.setUserId(userId);
        
        // 设置初始状态(1-在售)和浏览次数(0)
        product.setStatus(1);
        product.setViewCount(0);
        
        // 显式设置创建时间，解决created_time不能为null的问题
        product.setCreatedTime(LocalDateTime.now());
        
        // 插入数据库
        int result = productMapper.insert(product);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "商品添加失败");
        }
        
        // 返回完整的商品信息（包含自动生成的主键）
        return product;
    }
}