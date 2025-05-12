package com.xuchao.ershou.model.vo.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品成色分析VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConditionAnalysisVO {
    private Integer condition;
    private String conditionDesc;
    private Integer count;
    private Double percentage;
} 