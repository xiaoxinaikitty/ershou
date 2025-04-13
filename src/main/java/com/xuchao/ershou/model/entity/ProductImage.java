package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品图片实体类
 */
@Data
@TableName("product_image")
@Accessors(chain = true)
public class ProductImage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图片ID
     */
    @TableId(value = "image_id", type = IdType.AUTO)
    private Long imageId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 是否为主图(0否 1是)
     */
    private Integer isMain;

    /**
     * 图片排序
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
