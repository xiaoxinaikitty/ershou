package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dto.UserBehaviorDTO;
import com.xuchao.ershou.model.vo.ProductRecommendVO;
import com.xuchao.ershou.service.RecommendationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推荐控制器
 */
@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
@Slf4j
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * 记录用户行为
     * 
     * @param behaviorDTO 用户行为数据
     * @return 操作结果
     */
    @PostMapping("/behavior")
    public BaseResponse<Boolean> recordBehavior(@RequestBody @Valid UserBehaviorDTO behaviorDTO) {
        Long userId = CurrentUserUtils.getCurrentUserId();
        boolean result = recommendationService.recordUserBehavior(userId, behaviorDTO);
        return ResultUtils.success(result);
    }

    /**
     * 获取相似商品推荐
     * 
     * @param productId 商品ID
     * @param limit 限制数量
     * @return 相似商品列表
     */
    @GetMapping("/similar/{productId}")
    public BaseResponse<List<ProductRecommendVO>> getSimilarProducts(
            @PathVariable("productId") @NotNull(message = "商品ID不能为空") Long productId,
            @RequestParam(value = "limit", required = false) Integer limit) {
        List<ProductRecommendVO> result = recommendationService.getSimilarProducts(productId, limit);
        return ResultUtils.success(result);
    }

    /**
     * 获取个性化推荐
     * 
     * @param limit 限制数量
     * @return 推荐商品列表
     */
    @GetMapping("/personalized")
    public BaseResponse<List<ProductRecommendVO>> getPersonalizedRecommendations(
            @RequestParam(value = "limit", required = false) Integer limit) {
        Long userId = CurrentUserUtils.getCurrentUserId();
        List<ProductRecommendVO> result = recommendationService.getPersonalizedRecommendations(userId, limit);
        return ResultUtils.success(result);
    }

    /**
     * 获取热门推荐
     * 
     * @param limit 限制数量
     * @return 热门商品列表
     */
    @GetMapping("/hot")
    public BaseResponse<List<ProductRecommendVO>> getHotRecommendations(
            @RequestParam(value = "limit", required = false) Integer limit) {
        List<ProductRecommendVO> result = recommendationService.getHotRecommendations(limit);
        return ResultUtils.success(result);
    }

    /**
     * 记录推荐被点击
     * 
     * @param productId 商品ID
     * @param type 推荐类型(1相似商品 2猜你喜欢 3热门推荐 4新品推荐)
     * @return 操作结果
     */
    @PostMapping("/click")
    public BaseResponse<Boolean> recordRecommendationClick(
            @RequestParam("productId") @NotNull(message = "商品ID不能为空") Long productId,
            @RequestParam("type") @NotNull(message = "推荐类型不能为空") Integer type) {
        Long userId = CurrentUserUtils.getCurrentUserId();
        boolean result = recommendationService.recordRecommendationClick(userId, productId, type);
        return ResultUtils.success(result);
    }
}