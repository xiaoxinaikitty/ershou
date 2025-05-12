package com.xuchao.ershou.model.vo.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户活跃分析VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActiveAnalysisVO {
    private Integer userId;
    private String username;
    private Integer productCount;
    private Integer orderCount;
    private Integer favoriteCount;
    private Double activityScore;
} 