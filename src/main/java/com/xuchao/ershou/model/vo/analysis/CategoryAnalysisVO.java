package com.xuchao.ershou.model.vo.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品分类分析VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryAnalysisVO {
    private Integer categoryId;
    private String categoryName;
    private Integer count;
    private Double percentage;
} 