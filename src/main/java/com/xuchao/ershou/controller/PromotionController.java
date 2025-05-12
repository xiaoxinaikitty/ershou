package com.xuchao.ershou.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dao.promotion.PromotionAddDao;
import com.xuchao.ershou.model.dao.promotion.PromotionQueryDao;
import com.xuchao.ershou.model.dao.promotion.PromotionUpdateDao;
import com.xuchao.ershou.model.vo.PromotionVO;
import com.xuchao.ershou.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 营销活动控制器
 */
@RestController
@Validated
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    /**
     * 管理员添加营销活动
     * @param promotionAddDao 添加信息
     * @return 活动ID
     */
    @PostMapping("/admin/promotion/add")
    public BaseResponse<Long> addPromotion(@RequestBody @Valid PromotionAddDao promotionAddDao) {
        // 获取当前管理员ID
        Long adminId = CurrentUserUtils.getCurrentUserId();
        if (adminId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "管理员未登录");
        }
        
        // 验证是否为管理员
        if (!CurrentUserUtils.isAdmin(adminId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限添加营销活动");
        }
        
        Long promotionId = promotionService.addPromotion(adminId, promotionAddDao);
        return ResultUtils.success(promotionId);
    }

    /**
     * 管理员更新营销活动
     * @param promotionUpdateDao 更新信息
     * @return 是否成功
     */
    @PutMapping("/admin/promotion/update")
    public BaseResponse<Boolean> updatePromotion(@RequestBody @Valid PromotionUpdateDao promotionUpdateDao) {
        // 获取当前管理员ID
        Long adminId = CurrentUserUtils.getCurrentUserId();
        if (adminId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "管理员未登录");
        }
        
        // 验证是否为管理员
        if (!CurrentUserUtils.isAdmin(adminId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限更新营销活动");
        }
        
        boolean success = promotionService.updatePromotion(adminId, promotionUpdateDao);
        return ResultUtils.success(success);
    }

    /**
     * 管理员删除营销活动
     * @param promotionId 活动ID
     * @return 是否成功
     */
    @DeleteMapping("/admin/promotion/delete/{promotionId}")
    public BaseResponse<Boolean> deletePromotion(@PathVariable Long promotionId) {
        // 获取当前管理员ID
        Long adminId = CurrentUserUtils.getCurrentUserId();
        if (adminId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "管理员未登录");
        }
        
        // 验证是否为管理员
        if (!CurrentUserUtils.isAdmin(adminId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限删除营销活动");
        }
        
        boolean success = promotionService.deletePromotion(adminId, promotionId);
        return ResultUtils.success(success);
    }

    /**
     * 管理员更新营销活动状态
     * @param promotionId 活动ID
     * @param status 状态
     * @return 是否成功
     */
    @PutMapping("/admin/promotion/status")
    public BaseResponse<Boolean> updateStatus(
            @RequestParam Long promotionId,
            @RequestParam Integer status) {
        // 获取当前管理员ID
        Long adminId = CurrentUserUtils.getCurrentUserId();
        if (adminId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "管理员未登录");
        }
        
        // 验证是否为管理员
        if (!CurrentUserUtils.isAdmin(adminId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限更新活动状态");
        }
        
        boolean success = promotionService.updateStatus(adminId, promotionId, status);
        return ResultUtils.success(success);
    }

    /**
     * 管理员查询营销活动列表
     * @param queryDao 查询条件
     * @return 分页结果
     */
    @GetMapping("/admin/promotion/list")
    public BaseResponse<Map<String, Object>> getPromotionList(PromotionQueryDao queryDao) {
        // 获取当前管理员ID
        Long adminId = CurrentUserUtils.getCurrentUserId();
        if (adminId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "管理员未登录");
        }
        
        // 验证是否为管理员
        if (!CurrentUserUtils.isAdmin(adminId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限查询活动列表");
        }
        
        IPage<PromotionVO> page = promotionService.getPromotionList(queryDao);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", page.getRecords());
        result.put("total", page.getTotal());
        result.put("pageNum", page.getCurrent());
        result.put("pageSize", page.getSize());
        result.put("pages", page.getPages());
        result.put("hasNext", page.getCurrent() < page.getPages());
        result.put("hasPrevious", page.getCurrent() > 1);
        
        return ResultUtils.success(result);
    }

    /**
     * 查询营销活动详情（管理员和用户通用）
     * @param promotionId 活动ID
     * @return 活动详情
     */
    @GetMapping("/promotion/detail/{promotionId}")
    public BaseResponse<PromotionVO> getPromotionDetail(@PathVariable Long promotionId) {
        PromotionVO promotionDetail = promotionService.getPromotionDetail(promotionId);
        if (promotionDetail == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "活动不存在");
        }
        
        return ResultUtils.success(promotionDetail);
    }

    /**
     * 查询有效的营销活动列表（用于前台展示）
     * @param limit 限制数量，默认8个
     * @return 活动列表
     */
    @GetMapping("/promotion/active")
    public BaseResponse<List<PromotionVO>> getActivePromotions(
            @RequestParam(required = false) Integer limit) {
        List<PromotionVO> activePromotions = promotionService.getActivePromotions(limit);
        return ResultUtils.success(activePromotions);
    }
} 