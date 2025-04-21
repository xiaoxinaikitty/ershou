package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dao.product.ReleaseLocationDao;
import com.xuchao.ershou.model.vo.ReleaseLocationVO;

/**
 * 发布商品位置服务接口
 */
public interface ReleaseLocationService {
    
    /**
     * 添加发布商品位置
     * @param locationDao 位置信息
     * @return 添加的位置信息
     */
    ReleaseLocationVO addLocation(ReleaseLocationDao locationDao);
} 