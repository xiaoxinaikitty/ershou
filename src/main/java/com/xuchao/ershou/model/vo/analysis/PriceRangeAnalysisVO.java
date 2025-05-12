package com.xuchao.ershou.model.vo.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 价格区间分析VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceRangeAnalysisVO {
    private String range;
    private Double minPrice;
    private Double maxPrice;
    private Integer count;
    private Double percentage;
} 