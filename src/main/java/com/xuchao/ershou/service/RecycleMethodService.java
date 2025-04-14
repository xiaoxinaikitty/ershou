package com.xuchao.ershou.service;

import com.xuchao.ershou.model.dao.product.RecycleMethodAddDao;
import com.xuchao.ershou.model.vo.RecycleMethodVO;

/**
 * 商品交易方式服务接口
 */
public interface RecycleMethodService {
    
    /**
     * 添加商品交易方式
     * @param userId 当前登录用户ID
     * @param methodAddDao 交易方式信息
     * @return 添加的交易方式信息
     */
    RecycleMethodVO addRecycleMethod(Long userId, RecycleMethodAddDao methodAddDao);
}