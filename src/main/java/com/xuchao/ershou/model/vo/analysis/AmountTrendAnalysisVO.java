package com.xuchao.ershou.model.vo.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 金额趋势分析VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmountTrendAnalysisVO {
    private LocalDate date;
    private Double amount;
    private Integer count;
} 