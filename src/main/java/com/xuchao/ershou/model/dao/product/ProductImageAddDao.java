package com.xuchao.ershou.model.dao.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 添加商品图片的数据传输对象
 */
@Data
public class ProductImageAddDao {
    
    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    /**
     * 是否为主图(0否 1是)
     */
    private Integer isMain = 0;
    
    /**
     * 图片排序
     */
    private Integer sortOrder = 0;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 多个图片URL的列表
     */
    private List<String> imageUrls;
    
    /**
     * 获取图片URL列表，确保不返回null
     * 如果imageUrls为空但imageUrl不为空，会自动将imageUrl添加到列表中
     * 同时过滤掉空或null的URL
     * @return 图片URL列表，如果为null则返回空列表
     */
    public List<String> getImageUrls() {
        if (imageUrls == null) {
            imageUrls = new ArrayList<>();
        }
        
        // 如果列表为空但单张图片URL存在，自动添加
        if (imageUrls.isEmpty() && imageUrl != null && !imageUrl.trim().isEmpty()) {
            imageUrls.add(imageUrl);
        }
        
        // 过滤掉null和空字符串的URL
        if (!imageUrls.isEmpty()) {
            imageUrls = imageUrls.stream()
                .filter(url -> url != null && !url.trim().isEmpty())
                .collect(Collectors.toList());
        }
        
        return imageUrls;
    }
}
