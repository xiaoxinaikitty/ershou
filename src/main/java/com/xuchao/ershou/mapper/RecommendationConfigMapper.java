package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.RecommendationConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 推荐系统配置Mapper接口
 */
@Mapper
public interface RecommendationConfigMapper extends BaseMapper<RecommendationConfig> {
    
    /**
     * 根据配置键获取配置值
     * 
     * @param configKey 配置键
     * @return 配置值
     */
    @Select("SELECT config_value FROM recommendation_config WHERE config_key = #{configKey}")
    String getConfigValue(@Param("configKey") String configKey);
} 