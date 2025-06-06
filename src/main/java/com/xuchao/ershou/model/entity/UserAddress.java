package com.xuchao.ershou.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户地址表实体类
 */
@Data
@Accessors(chain = true)
@TableName("user_address")
public class UserAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "address_id", type = IdType.AUTO)
    private Long addressId;

    @TableField("user_id")
    private Long userId;

    /** 收货人姓名 */
    private String consignee;

    /** 所在地区 */
    private String region;

    /** 详细地址 */
    private String detail;

    /** 联系电话 */
    @TableField("contact_phone")
    private String contactPhone;

    /** 是否默认地址（0-否 1-是） */
    @TableField("is_default")
    private Boolean isDefault;

    /** 创建时间 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}