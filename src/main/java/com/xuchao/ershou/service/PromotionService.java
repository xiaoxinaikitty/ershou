package com.xuchao.ershou.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xuchao.ershou.model.dao.promotion.PromotionAddDao;
import com.xuchao.ershou.model.dao.promotion.PromotionQueryDao;
import com.xuchao.ershou.model.dao.promotion.PromotionUpdateDao;
import com.xuchao.ershou.model.entity.Promotion;
import com.xuchao.ershou.model.vo.PromotionVO;

import java.util.List;

/**
 * 营销活动Service接口
 */
public interface PromotionService extends IService<Promotion> {
    
    /**
     * 添加营销活动
     * @param adminId 管理员ID
     * @param promotionAddDao 添加信息
     * @return 活动ID
     */
    Long addPromotion(Long adminId, PromotionAddDao promotionAddDao);
    
    /**
     * 更新营销活动
     * @param adminId 管理员ID
     * @param promotionUpdateDao 更新信息
     * @return 是否成功
     */
    boolean updatePromotion(Long adminId, PromotionUpdateDao promotionUpdateDao);
    
    /**
     * 删除营销活动
     * @param adminId 管理员ID
     * @param promotionId 活动ID
     * @return 是否成功
     */
    boolean deletePromotion(Long adminId, Long promotionId);
    
    /**
     * 更新营销活动状态
     * @param adminId 管理员ID
     * @param promotionId 活动ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long adminId, Long promotionId, Integer status);
    
    /**
     * 分页查询营销活动列表
     * @param queryDao 查询条件
     * @return 分页结果
     */
    IPage<PromotionVO> getPromotionList(PromotionQueryDao queryDao);
    
    /**
     * 查询营销活动详情
     * @param promotionId 活动ID
     * @return 活动详情
     */
    PromotionVO getPromotionDetail(Long promotionId);
    
    /**
     * 查询有效的营销活动列表（用于前台展示）
     * @param limit 限制数量，默认8个
     * @return 活动列表
     */
    List<PromotionVO> getActivePromotions(Integer limit);
} 