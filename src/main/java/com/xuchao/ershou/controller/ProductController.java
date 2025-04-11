package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dao.product.ProductAddDao;
import com.xuchao.ershou.model.dao.product.ProductUpdateDao;
import com.xuchao.ershou.model.entity.Product;
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
}