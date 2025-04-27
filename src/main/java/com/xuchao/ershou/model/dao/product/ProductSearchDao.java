package com.xuchao.ershou.model.dao.product;

import lombok.Data;

/**
 * 商品搜索请求对象
 */
@Data
public class ProductSearchDao {
    
    /**
     * 当前页码，从1开始
     */
    private Integer pageNum = 1;
    
    /**
     * 每页数量，默认10
     */
    private Integer pageSize = 10;
    
    /**
     * 搜索关键词，用于匹配商品标题和描述
     */
    private String keyword;
    
    /**
     * 商品分类ID
     */
    private Integer categoryId;
    
    /**
     * 价格下限
     */
    private Double minPrice;
    
    /**
     * 价格上限
     */
    private Double maxPrice;
    
    /**
     * 排序字段 (price-价格, time-时间, view-浏览量)
     */
    private String sortField = "time";
    
    /**
     * 排序方式 (asc-升序, desc-降序)
     */
    private String sortOrder = "desc";
    
    /**
     * 商品状态筛选 (0下架 1在售 2已售)，默认查询在售商品
     */
    private Integer status = 1;
    
    /**
     * 最低成色等级(1-10)
     */
    private Integer minConditionLevel;
    
    /**
     * 地区筛选
     */
    private String location;
} 