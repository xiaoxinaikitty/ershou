package com.xuchao.ershou.model.dao.product;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 添加商品的数据传输对象
 */
@Data
public class ProductAddDao {
    
    /**
     * 商品标题
     */
    @NotBlank(message = "商品标题不能为空")
    @Size(max = 100, message = "商品标题最多100个字符")
    private String title;
    
    /**
     * 商品详细描述
     */
    private String description;
    
    /**
     * 商品价格
     */
    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    @DecimalMax(value = "9999999.99", message = "商品价格超出范围")
    private BigDecimal price;
    
    /**
     * 物品原价
     */
    @DecimalMin(value = "0", message = "物品原价不能为负数")
    private BigDecimal originalPrice;
    
    /**
     * 商品分类ID
     */
    @NotNull(message = "商品分类不能为空")
    private Integer categoryId;
    
    /**
     * 物品成色(1-10级)
     */
    @Min(value = 1, message = "物品成色最低为1级")
    @Max(value = 10, message = "物品成色最高为10级")
    private Integer conditionLevel;
    
    /**
     * 商品所在地
     */
    @Size(max = 200, message = "商品所在地最多200个字符")
    private String location;
    
    /**
     * 商品图片URL列表
     */
    private List<String> imageUrls;
    
    /**
     * 主图URL索引（对应imageUrls中的索引）
     * 默认第一张图为主图
     */
    private Integer mainImageIndex = 0;
}