package com.xuchao.ershou.model.vo.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 状态分析VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusAnalysisVO {
    private Integer status;
    private String statusDesc;
    private Integer count;
    private Double percentage;
} 