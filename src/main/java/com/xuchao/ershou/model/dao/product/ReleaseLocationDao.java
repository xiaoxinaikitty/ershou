package com.xuchao.ershou.model.dao.product;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 发布商品位置请求对象
 */
@Data
public class ReleaseLocationDao {
    
    /**
     * 省份名称
     */
    @NotBlank(message = "省份不能为空")
    private String province;
    
    /**
     * 城市名称
     */
    @NotBlank(message = "城市不能为空")
    private String city;
    
    /**
     * 区/县名称
     */
    @NotBlank(message = "区/县不能为空")
    private String district;
} 