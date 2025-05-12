package com.xuchao.ershou.model.vo.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 趋势分析VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendAnalysisVO {
    private LocalDate date;
    private Integer count;
} 