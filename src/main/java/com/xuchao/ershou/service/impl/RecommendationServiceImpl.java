package com.xuchao.ershou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xuchao.ershou.mapper.*;
import com.xuchao.ershou.model.dto.UserBehaviorDTO;
import com.xuchao.ershou.model.entity.*;
import com.xuchao.ershou.model.vo.ProductRecommendVO;
import com.xuchao.ershou.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final UserBehaviorMapper userBehaviorMapper;
    private final UserTagMapper userTagMapper;
    private final ProductTagMapper productTagMapper;
    private final RecommendationRecordMapper recommendationRecordMapper;
    private final SimilarProductsMapper similarProductsMapper;
    private final RecommendationConfigMapper configMapper;
    private final ProductMapper productMapper;

    /**
     * 记录用户行为
     *
     * @param userId 用户ID
     * @param behaviorDTO 用户行为数据
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean recordUserBehavior(Long userId, UserBehaviorDTO behaviorDTO) {
        // 创建并保存用户行为记录
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setProductId(behaviorDTO.getProductId());
        behavior.setBehaviorType(behaviorDTO.getBehaviorType());
        behavior.setBehaviorTime(LocalDateTime.now());
        behavior.setStayTime(behaviorDTO.getStayTime());
        behavior.setCreatedTime(LocalDateTime.now());
        userBehaviorMapper.insert(behavior);
        
        // 更新用户标签权重
        updateUserTags(userId, behaviorDTO.getProductId(), behaviorDTO.getBehaviorType());
        
        return true;
    }

    /**
     * 更新用户标签权重
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @param behaviorType 行为类型
     */
    private void updateUserTags(Long userId, Long productId, Integer behaviorType) {
        // 获取商品相关标签
        List<ProductTag> productTags = productTagMapper.selectList(
                new LambdaQueryWrapper<ProductTag>()
                        .eq(ProductTag::getProductId, productId));
        
        if (productTags.isEmpty()) {
            return;
        }
        
        // 行为权重系数
        BigDecimal weightFactor;
        switch (behaviorType) {
            case 1: // 浏览
                weightFactor = new BigDecimal("0.1");
                break;
            case 2: // 收藏
                weightFactor = new BigDecimal("0.3");
                break;
            case 3: // 加购
                weightFactor = new BigDecimal("0.5");
                break;
            case 4: // 购买
                weightFactor = new BigDecimal("1.0");
                break;
            case 5: // 评价
                weightFactor = new BigDecimal("0.8");
                break;
            default:
                weightFactor = new BigDecimal("0.1");
        }
        
        // 更新用户对应的标签权重
        for (ProductTag tag : productTags) {
            UserTag userTag = userTagMapper.selectOne(
                    new LambdaQueryWrapper<UserTag>()
                            .eq(UserTag::getUserId, userId)
                            .eq(UserTag::getTagName, tag.getTagName()));
            
            if (userTag == null) {
                // 创建新标签
                userTag = new UserTag();
                userTag.setUserId(userId);
                userTag.setTagName(tag.getTagName());
                userTag.setTagValue(tag.getTagValue().multiply(weightFactor));
                userTag.setUpdatedTime(LocalDateTime.now());
                userTagMapper.insert(userTag);
            } else {
                // 更新现有标签权重
                userTag.setTagValue(userTag.getTagValue().add(tag.getTagValue().multiply(weightFactor)));
                userTag.setUpdatedTime(LocalDateTime.now());
                userTagMapper.updateById(userTag);
            }
        }
    }

    /**
     * 根据商品ID获取相似商品推荐
     *
     * @param productId 商品ID
     * @param limit 限制数量
     * @return 相似商品列表
     */
    @Override
    public List<ProductRecommendVO> getSimilarProducts(Long productId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 6; // 默认返回6个
        }
        
        // 获取相似商品ID列表
        List<Long> similarProductIds = similarProductsMapper.findSimilarProductIds(productId, limit);
        
        if (similarProductIds.isEmpty()) {
            // 相似商品不足时，补充同类别热门商品
            Product product = productMapper.selectById(productId);
            if (product != null) {
                // 查询同类别商品
                QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda()
                        .eq(Product::getCategoryId, product.getCategoryId())
                        .eq(Product::getStatus, 1) // 在售状态
                        .ne(Product::getProductId, productId) // 排除当前商品
                        .orderByDesc(Product::getViewCount)
                        .last("LIMIT " + limit);
                
                List<Product> categoryProducts = productMapper.selectList(queryWrapper);
                similarProductIds = categoryProducts.stream()
                        .map(Product::getProductId)
                        .collect(Collectors.toList());
            }
        }
        
        // 查询商品详情并转换为VO
        return getProductRecommendVOs(similarProductIds, 1);
    }

    /**
     * 获取用户个性化推荐
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 推荐商品列表
     */
    @Override
    public List<ProductRecommendVO> getPersonalizedRecommendations(Long userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认返回10个
        }
        
        // 获取用户标签
        List<UserTag> userTags = userTagMapper.selectList(
                new LambdaQueryWrapper<UserTag>()
                        .eq(UserTag::getUserId, userId)
                        .orderByDesc(UserTag::getTagValue)
                        .last("LIMIT 10"));
        
        if (userTags.isEmpty()) {
            // 如果用户没有标签，返回热门推荐
            return getHotRecommendations(limit);
        }
        
        // 从用户最喜欢的标签开始匹配商品
        List<Long> recommendProductIds = new ArrayList<>();
        Set<Long> selectedIds = new HashSet<>();
        
        // 查找已购商品，避免重复推荐
        List<UserBehavior> purchasedBehaviors = userBehaviorMapper.selectList(
                new LambdaQueryWrapper<UserBehavior>()
                        .eq(UserBehavior::getUserId, userId)
                        .eq(UserBehavior::getBehaviorType, 4)); // 购买行为
        
        Set<Long> purchasedProductIds = purchasedBehaviors.stream()
                .map(UserBehavior::getProductId)
                .collect(Collectors.toSet());
        
        // 根据标签匹配商品
        for (UserTag userTag : userTags) {
            if (recommendProductIds.size() >= limit) {
                break;
            }
            
            List<ProductTag> matchingProducts = productTagMapper.selectList(
                    new LambdaQueryWrapper<ProductTag>()
                            .eq(ProductTag::getTagName, userTag.getTagName())
                            .orderByDesc(ProductTag::getTagValue)
                            .last("LIMIT 20"));
            
            for (ProductTag productTag : matchingProducts) {
                Long pid = productTag.getProductId();
                // 避免推荐已购商品和重复商品
                if (!purchasedProductIds.contains(pid) && !selectedIds.contains(pid)) {
                    // 检查商品状态是否在售
                    Product product = productMapper.selectById(pid);
                    if (product != null && product.getStatus() == 1) {
                        recommendProductIds.add(pid);
                        selectedIds.add(pid);
                        
                        // 记录推荐
                        recordRecommendation(userId, pid, 2, userTag.getTagValue().multiply(productTag.getTagValue()));
                        
                        if (recommendProductIds.size() >= limit) {
                            break;
                        }
                    }
                }
            }
        }
        
        // 如果推荐商品不足，补充热门商品
        if (recommendProductIds.size() < limit) {
            int needMore = limit - recommendProductIds.size();
            List<Product> hotProducts = productMapper.selectList(
                    new QueryWrapper<Product>()
                            .lambda()
                            .eq(Product::getStatus, 1)
                            .notIn(recommendProductIds.size() > 0, Product::getProductId, recommendProductIds)
                            .notIn(purchasedProductIds.size() > 0, Product::getProductId, purchasedProductIds)
                            .orderByDesc(Product::getViewCount)
                            .last("LIMIT " + needMore));
            
            List<Long> hotProductIds = hotProducts.stream()
                    .map(Product::getProductId)
                    .collect(Collectors.toList());
            
            // 记录热门推荐
            for (Long pid : hotProductIds) {
                recordRecommendation(userId, pid, 3, new BigDecimal("0.5"));
            }
            
            recommendProductIds.addAll(hotProductIds);
        }
        
        // 查询商品详情并转换为VO
        return getProductRecommendVOs(recommendProductIds, 2);
    }

    /**
     * 记录推荐
     */
    private void recordRecommendation(Long userId, Long productId, Integer type, BigDecimal score) {
        RecommendationRecord record = new RecommendationRecord();
        record.setUserId(userId);
        record.setProductId(productId);
        record.setRecommendationType(type);
        record.setScore(score);
        record.setIsClicked(false);
        record.setIsConverted(false);
        record.setRecommendTime(LocalDateTime.now());
        recommendationRecordMapper.insert(record);
    }

    /**
     * 获取热门推荐
     *
     * @param limit 限制数量
     * @return 热门商品列表
     */
    @Override
    public List<ProductRecommendVO> getHotRecommendations(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认返回10个
        }
        
        // 查询浏览量最高的商品
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Product::getStatus, 1) // 在售状态
                .orderByDesc(Product::getViewCount)
                .last("LIMIT " + limit);
        
        List<Product> hotProducts = productMapper.selectList(queryWrapper);
        List<Long> hotProductIds = hotProducts.stream()
                .map(Product::getProductId)
                .collect(Collectors.toList());
        
        // 查询商品详情并转换为VO
        return getProductRecommendVOs(hotProductIds, 3);
    }
    
    /**
     * 根据商品ID列表查询VO
     */
    private List<ProductRecommendVO> getProductRecommendVOs(List<Long> productIds, Integer recommendationType) {
        if (productIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Product> products = productMapper.selectBatchIds(productIds);
        
        // 保持原始顺序
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductId, p -> p));
        
        List<ProductRecommendVO> result = new ArrayList<>();
        for (Long id : productIds) {
            Product product = productMap.get(id);
            if (product != null) {
                ProductRecommendVO vo = new ProductRecommendVO();
                vo.setProductId(product.getProductId());
                vo.setTitle(product.getTitle());
                vo.setPrice(product.getPrice());
                vo.setOriginalPrice(product.getOriginalPrice());
                
                // 获取商品主图
                String mainImage = getProductMainImage(product.getProductId());
                vo.setMainImage(mainImage);
                
                vo.setRecommendationType(recommendationType);
                vo.setScore(new BigDecimal("1.0")); // 默认得分
                
                result.add(vo);
            }
        }
        
        return result;
    }
    
    /**
     * 获取商品主图
     */
    private String getProductMainImage(Long productId) {
        // 查询商品主图逻辑
        // 由于这里需要访问商品图片表，可以通过关联查询或者单独查询
        // 这里简化实现，假设已有单独查询方法
        return "http://example.com/images/product.jpg"; // 实际项目中应当替换为真实图片URL
    }
    
    /**
     * 记录推荐被点击
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @param recommendationType 推荐类型
     * @return 是否成功
     */
    @Override
    public boolean recordRecommendationClick(Long userId, Long productId, Integer recommendationType) {
        // 查找最近的推荐记录
        LambdaQueryWrapper<RecommendationRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RecommendationRecord::getUserId, userId)
                .eq(RecommendationRecord::getProductId, productId)
                .eq(RecommendationRecord::getRecommendationType, recommendationType)
                .eq(RecommendationRecord::getIsClicked, false)
                .orderByDesc(RecommendationRecord::getRecommendTime)
                .last("LIMIT 1");
        
        RecommendationRecord record = recommendationRecordMapper.selectOne(queryWrapper);
        
        if (record != null) {
            // 更新点击状态
            record.setIsClicked(true);
            record.setClickTime(LocalDateTime.now());
            recommendationRecordMapper.updateById(record);
            return true;
        } else {
            // 如果没有找到记录，创建一个新的
            record = new RecommendationRecord();
            record.setUserId(userId);
            record.setProductId(productId);
            record.setRecommendationType(recommendationType);
            record.setScore(new BigDecimal("1.0"));
            record.setIsClicked(true);
            record.setIsConverted(false);
            record.setRecommendTime(LocalDateTime.now());
            record.setClickTime(LocalDateTime.now());
            recommendationRecordMapper.insert(record);
            return true;
        }
    }
} 