package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.Category;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 商品分类Mapper接口
 */
public interface CategoryMapper extends BaseMapper<Category> {
    
    /**
     * 查询所有分类
     * @return 分类列表
     */
    List<Category> selectAllCategories();
    
    /**
     * 根据ID查询分类
     * @param id 分类ID
     * @return 分类信息
     */
    Category selectCategoryById(@Param("id") Integer id);
    
    /**
     * 根据名称查询分类
     * @param name 分类名称
     * @return 分类信息
     */
    Category selectCategoryByName(@Param("name") String name);
} 