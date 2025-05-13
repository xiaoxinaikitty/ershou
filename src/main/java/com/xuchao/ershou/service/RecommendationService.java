package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dto.UserBehaviorDTO;
import com.xuchao.ershou.model.vo.ProductRecommendVO;

import java.util.List;

/**
 * 推荐服务接口
 */
public interface RecommendationService {

    /**
     * 记录用户行为
     *
     * @param userId 用户ID
     * @param behaviorDTO 用户行为数据
     * @return 是否成功
     */
    boolean recordUserBehavior(Long userId, UserBehaviorDTO behaviorDTO);

    /**
     * 根据商品ID获取相似商品推荐
     *
     * @param productId 商品ID
     * @param limit 限制数量
     * @return 相似商品列表
     */
    List<ProductRecommendVO> getSimilarProducts(Long productId, Integer limit);

    /**
     * 获取用户个性化推荐
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 推荐商品列表
     */
    List<ProductRecommendVO> getPersonalizedRecommendations(Long userId, Integer limit);

    /**
     * 获取热门推荐
     *
     * @param limit 限制数量
     * @return 热门商品列表
     */
    List<ProductRecommendVO> getHotRecommendations(Integer limit);
    
    /**
     * 记录推荐被点击
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @param recommendationType 推荐类型
     * @return 是否成功
     */
    boolean recordRecommendationClick(Long userId, Long productId, Integer recommendationType);
} 