package com.xuchao.ershou.model.vo.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 热门商品VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotProductVO {
    private Integer productId;
    private String title;
    private String mainImageUrl;
    private Double price;
    private Integer viewCount;
    private Integer favoriteCount;
    private Integer soldCount;
    private Double hotScore;
} 