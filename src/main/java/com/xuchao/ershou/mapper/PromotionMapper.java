package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuchao.ershou.model.entity.Promotion;
import com.xuchao.ershou.model.vo.PromotionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 营销活动Mapper接口
 */
@Mapper
public interface PromotionMapper extends BaseMapper<Promotion> {
    
    /**
     * 分页查询营销活动列表
     * @param page 分页对象
     * @param title 活动标题（模糊查询）
     * @param promotionType 活动类型
     * @param status 状态
     * @param startTimeBegin 开始时间范围(起)
     * @param startTimeEnd 开始时间范围(止)
     * @param endTimeBegin 结束时间范围(起)
     * @param endTimeEnd 结束时间范围(止)
     * @return 分页结果
     */
    IPage<PromotionVO> selectPromotionList(
            Page<PromotionVO> page,
            @Param("title") String title,
            @Param("promotionType") Integer promotionType,
            @Param("status") Integer status,
            @Param("startTimeBegin") LocalDateTime startTimeBegin,
            @Param("startTimeEnd") LocalDateTime startTimeEnd,
            @Param("endTimeBegin") LocalDateTime endTimeBegin,
            @Param("endTimeEnd") LocalDateTime endTimeEnd
    );
    
    /**
     * 查询营销活动详情
     * @param promotionId 活动ID
     * @return 活动详情
     */
    PromotionVO selectPromotionDetail(@Param("promotionId") Long promotionId);
    
    /**
     * 查询有效的营销活动列表（用于前台展示）
     * @param currentTime 当前时间
     * @param limit 限制数量
     * @return 活动列表
     */
    List<PromotionVO> selectActivePromotions(
            @Param("currentTime") LocalDateTime currentTime,
            @Param("limit") Integer limit
    );
} 