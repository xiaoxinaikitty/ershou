package com.xuchao.ershou.model.dao.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 举报商品的数据传输对象
 */
@Data
public class ProductReportAddDao {
    
    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    /**
     * 举报类型(1虚假商品 2违禁品 3侵权等)
     */
    @NotNull(message = "举报类型不能为空")
    private Integer reportType;
    
    /**
     * 举报内容
     */
    @Size(max = 500, message = "举报内容最多500个字符")
    private String reportContent;
}
