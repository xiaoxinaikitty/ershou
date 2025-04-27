package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.mapper.ProductMapper;
import com.xuchao.ershou.model.dao.product.ProductAddDao;
import com.xuchao.ershou.model.dao.product.ProductPageQueryDao;
import com.xuchao.ershou.model.dao.product.ProductSearchDao;
import com.xuchao.ershou.model.dao.product.ProductUpdateDao;
import com.xuchao.ershou.model.entity.Product;
import com.xuchao.ershou.model.vo.PageResult;
import com.xuchao.ershou.model.vo.ProductPageVO;
import com.xuchao.ershou.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xuchao.ershou.common.ErrorCode;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    
    @Override
    @Transactional
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
    
    @Override
    public Product getProductById(Long productId) {
        // 参数校验
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品ID无效");
        }
        
        // 查询商品信息
        Product product = productMapper.selectProductById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        
        // 增加商品浏览次数
        // 注意：在高并发环境下，这里可以考虑使用Redis等缓存策略优化
        product.setViewCount(product.getViewCount() + 1);
        productMapper.updateById(product);
        
        return product;
    }
    
    @Override
    @Transactional
    public Product updateProduct(Long userId, ProductUpdateDao productUpdateDao) {
        // 参数校验
        if (productUpdateDao == null || productUpdateDao.getProductId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品信息不完整");
        }
        
        // 查询原有商品是否存在
        Product existProduct = productMapper.selectProductById(productUpdateDao.getProductId());
        if (existProduct == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        
        // 校验当前用户是否为商品发布者
        if (!existProduct.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限修改他人发布的商品");
        }
        
        // 创建更新对象
        Product updateProduct = new Product();
        updateProduct.setProductId(productUpdateDao.getProductId());
        
        // 选择性更新字段（只更新非空字段）
        if (productUpdateDao.getTitle() != null) {
            updateProduct.setTitle(productUpdateDao.getTitle());
        }
        
        if (productUpdateDao.getDescription() != null) {
            updateProduct.setDescription(productUpdateDao.getDescription());
        }
        
        if (productUpdateDao.getPrice() != null) {
            updateProduct.setPrice(productUpdateDao.getPrice());
        }
        
        if (productUpdateDao.getOriginalPrice() != null) {
            updateProduct.setOriginalPrice(productUpdateDao.getOriginalPrice());
        }
        
        if (productUpdateDao.getCategoryId() != null) {
            updateProduct.setCategoryId(productUpdateDao.getCategoryId());
        }
        
        if (productUpdateDao.getConditionLevel() != null) {
            updateProduct.setConditionLevel(productUpdateDao.getConditionLevel());
        }
        
        if (productUpdateDao.getStatus() != null) {
            updateProduct.setStatus(productUpdateDao.getStatus());
        }
        
        if (productUpdateDao.getLocation() != null) {
            updateProduct.setLocation(productUpdateDao.getLocation());
        }
        
        // 执行更新
        int result = productMapper.updateProduct(updateProduct);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "商品更新失败");
        }
        
        // 返回更新后的完整商品信息
        return productMapper.selectProductById(productUpdateDao.getProductId());
    }
    
    @Override
    @Transactional
    public boolean deleteProduct(Long userId, Long productId) {
        // 参数校验
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品ID无效");
        }
        
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 查询商品是否存在
        Product existProduct = productMapper.selectProductById(productId);
        if (existProduct == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        
        // 校验当前用户是否为商品发布者
        if (!existProduct.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限下架他人发布的商品");
        }
        
        // 如果商品已经是下架状态，直接返回成功
        if (existProduct.getStatus() != null && existProduct.getStatus() == 0) {
            return true;
        }
        
        // 创建更新对象，将商品状态设置为下架(0)
        Product updateProduct = new Product();
        updateProduct.setProductId(productId);
        updateProduct.setStatus(0); // 0表示下架状态
        
        // 执行更新
        int result = productMapper.updateProduct(updateProduct);
        return result > 0;
    }
    
    @Override
    public PageResult<ProductPageVO> pageProducts(ProductPageQueryDao queryParams) {
        // 参数校验
        if (queryParams == null) {
            queryParams = new ProductPageQueryDao();
        }
        
        // 确保页码和每页大小有效
        if (queryParams.getPageNum() == null || queryParams.getPageNum() < 1) {
            queryParams.setPageNum(1);
        }
        
        if (queryParams.getPageSize() == null || queryParams.getPageSize() < 1) {
            queryParams.setPageSize(10);
        }
        
        // 限制每页最大数量，防止大量数据查询影响性能
        if (queryParams.getPageSize() > 50) {
            queryParams.setPageSize(50);
        }
        
        // 计算分页偏移量
        int offset = (queryParams.getPageNum() - 1) * queryParams.getPageSize();
        int limit = queryParams.getPageSize();
        
        // 查询符合条件的总记录数
        long total = productMapper.countProducts(queryParams);
        
        // 如果没有记录，直接返回空结果
        if (total == 0) {
            return new PageResult<>(queryParams.getPageNum(), queryParams.getPageSize(), 0L, List.of());
        }
        
        // 查询当前页数据
        List<ProductPageVO> productList = productMapper.selectProductPage(queryParams, offset, limit);
        
        // 构建并返回分页结果
        return new PageResult<>(queryParams.getPageNum(), queryParams.getPageSize(), total, productList);
    }
    
    @Override
    public PageResult<ProductPageVO> searchProducts(ProductSearchDao searchParams) {
        // 参数校验
        if (searchParams == null) {
            searchParams = new ProductSearchDao();
        }
        
        // 确保页码和每页大小有效
        if (searchParams.getPageNum() == null || searchParams.getPageNum() < 1) {
            searchParams.setPageNum(1);
        }
        
        if (searchParams.getPageSize() == null || searchParams.getPageSize() < 1) {
            searchParams.setPageSize(10);
        }
        
        // 限制每页最大数量，防止大量数据查询影响性能
        if (searchParams.getPageSize() > 50) {
            searchParams.setPageSize(50);
        }
        
        // 计算分页偏移量
        int offset = (searchParams.getPageNum() - 1) * searchParams.getPageSize();
        int limit = searchParams.getPageSize();
        
        // 查询符合条件的总记录数
        long total = productMapper.countSearchProducts(searchParams);
        
        // 如果没有记录，直接返回空结果
        if (total == 0) {
            return new PageResult<>(searchParams.getPageNum(), searchParams.getPageSize(), 0L, List.of());
        }
        
        // 查询当前页数据
        List<ProductPageVO> productList = productMapper.searchProducts(searchParams, offset, limit);
        
        // 构建并返回分页结果
        return new PageResult<>(searchParams.getPageNum(), searchParams.getPageSize(), total, productList);
    }
    
    @Override
    public long getProductCount(Integer status) {
        // 创建查询参数对象
        ProductPageQueryDao queryParams = new ProductPageQueryDao();
        
        // 设置商品状态筛选条件
        queryParams.setStatus(status);
        
        // 通过Mapper查询总数并返回
        return productMapper.countProducts(queryParams);
    }
}