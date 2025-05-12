package com.xuchao.ershou.model.vo.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 自定义日期范围分析VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomAnalysisVO {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer newUsers;
    private Integer newProducts;
    private Integer newOrders;
    private Double orderAmount;
    private List<CategoryAnalysisVO> categoryAnalysis;
    private List<StatusAnalysisVO> productStatusAnalysis;
    private List<StatusAnalysisVO> orderStatusAnalysis;
    private List<TrendAnalysisVO> userRegisterTrend;
    private List<TrendAnalysisVO> productTrend;
    private List<TrendAnalysisVO> orderTrend;
    private List<AmountTrendAnalysisVO> orderAmountTrend;
} 