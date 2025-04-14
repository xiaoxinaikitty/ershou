package com.xuchao.ershou.model.dao.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 添加商品交易方式的数据传输对象
 */
@Data
public class RecycleMethodAddDao {
    
    /**
     * 方式名称
     */
    @NotBlank(message = "方式名称不能为空")
    @Size(max = 20, message = "方式名称最多20个字符")
    private String methodName;
    
    /**
     * 方式描述
     */
    @NotBlank(message = "方式描述不能为空")
    @Size(max = 255, message = "方式描述最多255个字符")
    private String methodDesc;
}