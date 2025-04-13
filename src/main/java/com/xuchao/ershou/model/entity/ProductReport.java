package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品举报实体类
 */
@Data
@TableName("product_report")
@Accessors(chain = true)
public class ProductReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 举报ID
     */
    @TableId(value = "report_id", type = IdType.AUTO)
    private Long reportId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 举报用户ID
     */
    private Long userId;

    /**
     * 举报类型(1虚假商品 2违禁品 3侵权等)
     */
    private Integer reportType;

    /**
     * 举报内容
     */
    private String reportContent;

    /**
     * 处理状态(0未处理 1已处理)
     */
    private Integer status;

    /**
     * 举报时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;
}
