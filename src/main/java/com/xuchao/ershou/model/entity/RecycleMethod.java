package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 商品交易方式实体类
 */
@Data
@TableName("recycle_method")
@Accessors(chain = true)
public class RecycleMethod implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 方式ID
     */
    @TableId(value = "method_id", type = IdType.AUTO)
    private Integer methodId;

    /**
     * 方式名称
     */
    @TableField("method_name")
    private String methodName;

    /**
     * 方式描述
     */
    @TableField("method_desc")
    private String methodDesc;
}