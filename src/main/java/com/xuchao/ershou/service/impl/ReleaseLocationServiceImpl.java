package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.mapper.ReleaseLocationMapper;
import com.xuchao.ershou.model.dao.product.ReleaseLocationDao;
import com.xuchao.ershou.model.entity.ReleaseLocation;
import com.xuchao.ershou.model.vo.ReleaseLocationVO;
import com.xuchao.ershou.service.ReleaseLocationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 发布商品位置服务实现类
 */
@Service
public class ReleaseLocationServiceImpl implements ReleaseLocationService {

    @Autowired
    private ReleaseLocationMapper releaseLocationMapper;
    
    @Override
    @Transactional
    public ReleaseLocationVO addLocation(ReleaseLocationDao locationDao) {
        // 参数校验
        if (locationDao == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "位置信息不能为空");
        }
        
        if (locationDao.getProvince() == null || locationDao.getProvince().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "省份不能为空");
        }
        
        if (locationDao.getCity() == null || locationDao.getCity().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "城市不能为空");
        }
        
        if (locationDao.getDistrict() == null || locationDao.getDistrict().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "区/县不能为空");
        }
        
        // 创建实体并设置属性
        ReleaseLocation location = new ReleaseLocation();
        BeanUtils.copyProperties(locationDao, location);
        
        // 插入数据库
        int result = releaseLocationMapper.insertLocation(location);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加位置信息失败");
        }
        
        // 构造返回对象
        ReleaseLocationVO locationVO = new ReleaseLocationVO();
        BeanUtils.copyProperties(location, locationVO);
        
        return locationVO;
    }
} 