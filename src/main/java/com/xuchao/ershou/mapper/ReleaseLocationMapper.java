package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.ReleaseLocation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 发布商品位置Mapper接口
 */
@Mapper
public interface ReleaseLocationMapper extends BaseMapper<ReleaseLocation> {
    
    /**
     * 插入位置信息并返回主键ID
     * @param releaseLocation 位置信息
     * @return 影响行数
     */
    int insertLocation(ReleaseLocation releaseLocation);
} 