package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dao.product.ProductAddDao;
import com.xuchao.ershou.model.dao.product.ProductPageQueryDao;
import com.xuchao.ershou.model.dao.product.ProductSearchDao;
import com.xuchao.ershou.model.dao.product.ProductUpdateDao;
import com.xuchao.ershou.model.entity.Product;
import com.xuchao.ershou.model.vo.PageResult;
import com.xuchao.ershou.model.vo.ProductPageVO;
import com.xuchao.ershou.service.ProductService;
import com.xuchao.ershou.common.ErrorCode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * 添加商品
     * @param productAddDao 商品信息
     * @return 处理结果
     */
    @PostMapping("/add")
    public BaseResponse<Product> addProduct(@RequestBody @Valid ProductAddDao productAddDao) {
        // 获取当前登录用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 调用服务层添加商品
        Product addedProduct = productService.addProduct(currentUserId, productAddDao);
        
        return ResultUtils.success(addedProduct);
    }
    
    /**
     * 根据ID查询商品详情
     * @param productId 商品ID
     * @return 商品详细信息
     */
    @GetMapping("/detail/{productId}")
    public BaseResponse<Product> getProductDetail(@PathVariable Long productId) {
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品ID无效");
        }
        
        // 调用服务层查询商品详情
        Product product = productService.getProductById(productId);
        
        return ResultUtils.success(product);
    }
    
    /**
     * 更新商品信息
     * @param productUpdateDao 商品更新信息
     * @return 更新后的商品信息
     */
    @PutMapping("/update")
    public BaseResponse<Product> updateProduct(@RequestBody @Valid ProductUpdateDao productUpdateDao) {
        // 获取当前登录用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 调用服务层更新商品
        Product updatedProduct = productService.updateProduct(currentUserId, productUpdateDao);
        
        return ResultUtils.success(updatedProduct);
    }

    /**
     * 删除商品（实际为下架商品）
     * @param productId 商品ID
     * @return 处理结果
     */
    @DeleteMapping("/delete/{productId}")
    public BaseResponse<String> deleteProduct(@PathVariable Long productId) {
        // 获取当前登录用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 调用服务层删除商品（将状态设置为下架）
        boolean result = productService.deleteProduct(currentUserId, productId);
        
        if (result) {
            return ResultUtils.success("商品删除成功");
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "商品删除失败");
        }
    }
    
    /**
     * 分页查询商品列表
     * @param queryParams 查询参数
     * @return 分页结果
     */
    @GetMapping("/list")
    public BaseResponse<PageResult<ProductPageVO>> listProducts(ProductPageQueryDao queryParams) {
        // 调用服务层分页查询商品
        PageResult<ProductPageVO> pageResult = productService.pageProducts(queryParams);
        
        return ResultUtils.success(pageResult);
    }
    
    /**
     * 用户发布商品列表
     * @param queryParams 查询参数
     * @return 分页结果
     */
    @GetMapping("/my-list")
    public BaseResponse<PageResult<ProductPageVO>> listMyProducts(ProductPageQueryDao queryParams) {
        // 获取当前登录用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 设置查询条件为当前用户ID
        queryParams.setSellerId(currentUserId);
        
        // 调用服务层分页查询商品
        PageResult<ProductPageVO> pageResult = productService.pageProducts(queryParams);
        
        return ResultUtils.success(pageResult);
    }
    
    /**
     * 搜索商品（根据标题和描述搜索）
     * @param searchParams 搜索参数
     * @return 分页结果
     */
    @GetMapping("/search")
    public BaseResponse<PageResult<ProductPageVO>> searchProducts(ProductSearchDao searchParams) {
        // 调用服务层搜索商品
        PageResult<ProductPageVO> pageResult = productService.searchProducts(searchParams);
        
        return ResultUtils.success(pageResult);
    }
    
    /**
     * 获取商品总数
     * @param status 商品状态(0下架 1在售 2已售)，可为null，表示获取所有状态的商品数量
     * @return 商品总数
     */
    @GetMapping("/count")
    public BaseResponse<Long> getProductCount(@RequestParam(required = false) Integer status) {
        // 调用服务层获取商品总数
        long count = productService.getProductCount(status);
        
        return ResultUtils.success(count);
    }
}