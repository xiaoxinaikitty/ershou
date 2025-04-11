package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品信息表实体类
 */
@Data
@TableName("product")
@Accessors(chain = true)
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @TableId(value = "product_id", type = IdType.AUTO)
    private Long productId;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品详细描述
     */
    private String description;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 物品原价
     */
    private BigDecimal originalPrice;

    /**
     * 商品分类ID
     */
    private Integer categoryId;

    /**
     * 发布用户ID
     */
    private Long userId;

    /**
     * 物品成色(1-10级)
     */
    private Integer conditionLevel;

    /**
     * 商品状态(0下架 1在售 2已售)
     */
    private Integer status;

    /**
     * 商品所在地
     */
    private String location;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 发布时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}