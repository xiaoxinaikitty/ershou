package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dao.product.ProductAddDao;
import com.xuchao.ershou.model.entity.Product;
import com.xuchao.ershou.service.ProductService;
import com.xuchao.ershou.common.ErrorCode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}