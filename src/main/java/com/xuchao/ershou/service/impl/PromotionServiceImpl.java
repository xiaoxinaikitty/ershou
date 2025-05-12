package com.xuchao.ershou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuchao.ershou.common.enums.PromotionStatusEnum;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.mapper.PromotionImageMapper;
import com.xuchao.ershou.mapper.PromotionMapper;
import com.xuchao.ershou.model.dao.promotion.PromotionAddDao;
import com.xuchao.ershou.model.dao.promotion.PromotionImageAddDao;
import com.xuchao.ershou.model.dao.promotion.PromotionQueryDao;
import com.xuchao.ershou.model.dao.promotion.PromotionUpdateDao;
import com.xuchao.ershou.model.entity.Promotion;
import com.xuchao.ershou.model.entity.PromotionImage;
import com.xuchao.ershou.model.vo.PromotionVO;
import com.xuchao.ershou.service.PromotionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 营销活动Service实现类
 */
@Service
public class PromotionServiceImpl extends ServiceImpl<PromotionMapper, Promotion> implements PromotionService {

    @Autowired
    private PromotionMapper promotionMapper;

    @Autowired
    private PromotionImageMapper promotionImageMapper;

    /**
     * 添加营销活动
     *
     * @param adminId        管理员ID
     * @param promotionAddDao 添加信息
     * @return 活动ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addPromotion(Long adminId, PromotionAddDao promotionAddDao) {
        // 验证结束时间必须大于开始时间
        if (promotionAddDao.getEndTime().isBefore(promotionAddDao.getStartTime())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "结束时间必须大于开始时间");
        }

        // 构建并保存营销活动
        Promotion promotion = new Promotion();
        BeanUtils.copyProperties(promotionAddDao, promotion);
        promotion.setCreatedBy(adminId);
        
        boolean success = this.save(promotion);
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加营销活动失败");
        }

        // 处理活动图片
        if (!CollectionUtils.isEmpty(promotionAddDao.getImages())) {
            List<PromotionImage> imageList = promotionAddDao.getImages().stream().map(imageDao -> {
                PromotionImage image = new PromotionImage();
                BeanUtils.copyProperties(imageDao, image);
                image.setPromotionId(promotion.getPromotionId());
                return image;
            }).collect(Collectors.toList());

            // 批量插入图片
            int rows = promotionImageMapper.batchInsert(imageList);
            if (rows != imageList.size()) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加活动图片失败");
            }
        }

        return promotion.getPromotionId();
    }

    /**
     * 更新营销活动
     *
     * @param adminId           管理员ID
     * @param promotionUpdateDao 更新信息
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePromotion(Long adminId, PromotionUpdateDao promotionUpdateDao) {
        // 验证营销活动是否存在
        Long promotionId = promotionUpdateDao.getPromotionId();
        Promotion promotion = this.getById(promotionId);
        if (promotion == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "营销活动不存在");
        }

        // 验证结束时间必须大于开始时间
        if (promotionUpdateDao.getEndTime().isBefore(promotionUpdateDao.getStartTime())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "结束时间必须大于开始时间");
        }

        // 修改活动基本信息
        Promotion updatePromotion = new Promotion();
        BeanUtils.copyProperties(promotionUpdateDao, updatePromotion);
        boolean success = this.updateById(updatePromotion);
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新营销活动失败");
        }

        // 处理活动图片：先删除所有原有图片，再重新添加
        LambdaQueryWrapper<PromotionImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PromotionImage::getPromotionId, promotionId);
        promotionImageMapper.delete(queryWrapper);

        // 添加新图片
        if (!CollectionUtils.isEmpty(promotionUpdateDao.getImages())) {
            List<PromotionImage> imageList = promotionUpdateDao.getImages().stream().map(imageDao -> {
                PromotionImage image = new PromotionImage();
                BeanUtils.copyProperties(imageDao, image);
                image.setPromotionId(promotion.getPromotionId());
                return image;
            }).collect(Collectors.toList());

            // 批量插入图片
            int rows = promotionImageMapper.batchInsert(imageList);
            if (rows != imageList.size()) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新活动图片失败");
            }
        }

        return true;
    }

    /**
     * 删除营销活动
     *
     * @param adminId    管理员ID
     * @param promotionId 活动ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePromotion(Long adminId, Long promotionId) {
        // 验证营销活动是否存在
        Promotion promotion = this.getById(promotionId);
        if (promotion == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "营销活动不存在");
        }

        // 删除营销活动
        boolean success = this.removeById(promotionId);
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除营销活动失败");
        }

        // 删除关联的所有图片
        LambdaQueryWrapper<PromotionImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PromotionImage::getPromotionId, promotionId);
        promotionImageMapper.delete(queryWrapper);

        return true;
    }

    /**
     * 更新营销活动状态
     *
     * @param adminId    管理员ID
     * @param promotionId 活动ID
     * @param status     状态
     * @return 是否成功
     */
    @Override
    public boolean updateStatus(Long adminId, Long promotionId, Integer status) {
        // 验证营销活动是否存在
        Promotion promotion = this.getById(promotionId);
        if (promotion == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "营销活动不存在");
        }

        // 验证状态值是否合法
        if (!PromotionStatusEnum.isValidStatus(status)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "状态值不合法");
        }

        // 修改状态
        LambdaUpdateWrapper<Promotion> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Promotion::getPromotionId, promotionId);
        updateWrapper.set(Promotion::getStatus, status);
        return this.update(updateWrapper);
    }

    /**
     * 分页查询营销活动列表
     *
     * @param queryDao 查询条件
     * @return 分页结果
     */
    @Override
    public IPage<PromotionVO> getPromotionList(PromotionQueryDao queryDao) {
        // 创建分页对象
        Page<PromotionVO> page = new Page<>(queryDao.getPageNum(), queryDao.getPageSize());

        // 调用Mapper查询
        IPage<PromotionVO> pageResult = promotionMapper.selectPromotionList(
                page,
                queryDao.getTitle(),
                queryDao.getPromotionType(),
                queryDao.getStatus(),
                queryDao.getStartTimeBegin(),
                queryDao.getStartTimeEnd(),
                queryDao.getEndTimeBegin(),
                queryDao.getEndTimeEnd()
        );

        // 填充图片信息
        if (pageResult.getRecords() != null && !pageResult.getRecords().isEmpty()) {
            for (PromotionVO promotion : pageResult.getRecords()) {
                // 查询并设置图片列表
                promotion.setImages(promotionImageMapper.selectImagesByPromotionId(promotion.getPromotionId()));
            }
        }

        return pageResult;
    }

    /**
     * 查询营销活动详情
     *
     * @param promotionId 活动ID
     * @return 活动详情
     */
    @Override
    public PromotionVO getPromotionDetail(Long promotionId) {
        PromotionVO promotionDetail = promotionMapper.selectPromotionDetail(promotionId);
        if (promotionDetail != null) {
            // 查询并设置图片列表
            promotionDetail.setImages(promotionImageMapper.selectImagesByPromotionId(promotionId));
        }
        return promotionDetail;
    }

    /**
     * 查询有效的营销活动列表
     *
     * @param limit 限制数量，默认8个
     * @return 活动列表
     */
    @Override
    public List<PromotionVO> getActivePromotions(Integer limit) {
        // 设置默认限制
        if (limit == null || limit <= 0) {
            limit = 8;
        }

        // 查询当前有效的活动
        List<PromotionVO> activePromotions = promotionMapper.selectActivePromotions(
                LocalDateTime.now(),
                limit
        );

        // 填充图片信息
        if (!CollectionUtils.isEmpty(activePromotions)) {
            for (PromotionVO promotion : activePromotions) {
                // 查询并设置图片列表
                promotion.setImages(promotionImageMapper.selectImagesByPromotionId(promotion.getPromotionId()));
            }
        }

        return activePromotions;
    }
} 