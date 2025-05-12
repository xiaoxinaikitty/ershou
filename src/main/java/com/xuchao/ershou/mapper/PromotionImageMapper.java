package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.PromotionImage;
import com.xuchao.ershou.model.vo.PromotionImageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 营销活动图片Mapper接口
 */
@Mapper
public interface PromotionImageMapper extends BaseMapper<PromotionImage> {
    
    /**
     * 查询营销活动的图片列表
     * @param promotionId 活动ID
     * @return 图片列表
     */
    List<PromotionImageVO> selectImagesByPromotionId(@Param("promotionId") Long promotionId);
    
    /**
     * 查询指定类型的图片列表
     * @param promotionId 活动ID
     * @param imageType 图片类型
     * @return 图片列表
     */
    List<PromotionImageVO> selectImagesByType(
            @Param("promotionId") Long promotionId,
            @Param("imageType") Integer imageType
    );
    
    /**
     * 批量添加图片
     * @param images 图片列表
     * @return 影响行数
     */
    int batchInsert(@Param("images") List<PromotionImage> images);
} 