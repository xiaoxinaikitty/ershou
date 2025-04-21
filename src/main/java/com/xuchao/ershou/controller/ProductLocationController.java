package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dao.product.ReleaseLocationDao;
import com.xuchao.ershou.model.vo.ReleaseLocationVO;
import com.xuchao.ershou.service.ReleaseLocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品位置控制器
 */
@RestController
@RequestMapping("/product/location")
public class ProductLocationController {
    
    @Autowired
    private ReleaseLocationService releaseLocationService;
    
    /**
     * 添加发布商品位置
     * @param locationDao 位置信息
     * @return 添加的位置信息
     */
    @PostMapping("/add")
    public BaseResponse<ReleaseLocationVO> addLocation(@RequestBody @Valid ReleaseLocationDao locationDao) {
        // 获取当前登录用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 调用服务添加位置信息
        ReleaseLocationVO locationVO = releaseLocationService.addLocation(locationDao);
        
        return ResultUtils.success(locationVO);
    }
} 