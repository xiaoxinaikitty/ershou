package com.xuchao.ershou.service;

import com.xuchao.ershou.model.vo.analysis.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 数据分析服务接口
 */
public interface DataAnalysisService {
    
    /**
     * 获取商品分类统计
     * @return 分类统计列表
     */
    List<CategoryAnalysisVO> getProductCategoryAnalysis();

    /**
     * 获取商品价格区间统计
     * @return 价格区间统计列表
     */
    List<PriceRangeAnalysisVO> getProductPriceRangeAnalysis();

    /**
     * 获取商品成色统计
     * @return 成色统计列表
     */
    List<ConditionAnalysisVO> getProductConditionAnalysis();

    /**
     * 获取商品状态统计
     * @return 状态统计列表
     */
    List<StatusAnalysisVO> getProductStatusAnalysis();

    /**
     * 获取商品发布趋势
     * @param days 天数
     * @return 趋势统计列表
     */
    List<TrendAnalysisVO> getProductTrend(Integer days);

    /**
     * 获取用户注册趋势
     * @param days 天数
     * @return 趋势统计列表
     */
    List<TrendAnalysisVO> getUserRegisterTrend(Integer days);

    /**
     * 获取订单趋势
     * @param days 天数
     * @return 趋势统计列表
     */
    List<TrendAnalysisVO> getOrderTrend(Integer days);

    /**
     * 获取订单金额趋势
     * @param days 天数
     * @return 金额趋势统计列表
     */
    List<AmountTrendAnalysisVO> getOrderAmountTrend(Integer days);

    /**
     * 获取订单状态统计
     * @return 状态统计列表
     */
    List<StatusAnalysisVO> getOrderStatusAnalysis();

    /**
     * 获取活跃用户统计
     * @param days 天数
     * @return 活跃用户统计列表
     */
    List<UserActiveAnalysisVO> getUserActiveAnalysis(Integer days);

    /**
     * 获取数据摘要
     * @return 数据摘要
     */
    DataSummaryVO getDataSummary();

    /**
     * 获取热门商品
     * @param limit 数量限制
     * @return 热门商品列表
     */
    List<HotProductVO> getHotProducts(Integer limit);

    /**
     * 获取自定义日期范围的数据分析
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 自定义分析结果
     */
    CustomAnalysisVO getCustomAnalysis(LocalDate startDate, LocalDate endDate);
} 