package com.xuchao.ershou.model.dao.product;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 更新商品的数据传输对象
 */
@Data
public class ProductUpdateDao {
    
    /**
     * 商品ID（必填）
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    /**
     * 商品标题（选填）
     */
    @Size(max = 100, message = "商品标题最多100个字符")
    private String title;
    
    /**
     * 商品详细描述（选填）
     */
    private String description;
    
    /**
     * 商品价格（选填）
     */
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    @DecimalMax(value = "9999999.99", message = "商品价格超出范围")
    private BigDecimal price;
    
    /**
     * 物品原价（选填）
     */
    @DecimalMin(value = "0", message = "物品原价不能为负数")
    private BigDecimal originalPrice;
    
    /**
     * 商品分类ID（选填）
     */
    private Integer categoryId;
    
    /**
     * 物品成色(1-10级)（选填）
     */
    @Min(value = 1, message = "物品成色最低为1级")
    @Max(value = 10, message = "物品成色最高为10级")
    private Integer conditionLevel;
    
    /**
     * 商品状态(0下架 1在售 2已售)（选填）
     */
    @Min(value = 0, message = "商品状态值无效")
    @Max(value = 2, message = "商品状态值无效")
    private Integer status;
    
    /**
     * 商品所在地（选填）
     */
    @Size(max = 200, message = "商品所在地最多200个字符")
    private String location;
}