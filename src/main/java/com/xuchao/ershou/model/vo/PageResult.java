package com.xuchao.ershou.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通用分页结果包装类
 * @param <T> 结果数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    
    /**
     * 当前页码
     */
    private Integer pageNum;
    
    /**
     * 每页大小
     */
    private Integer pageSize;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 总页数
     */
    private Integer pages;
    
    /**
     * 当前页数据
     */
    private List<T> list;
    
    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;
    
    /**
     * 是否有下一页
     */
    private Boolean hasNext;
    
    /**
     * 创建分页结果对象
     * @param pageNum 当前页码
     * @param pageSize 每页大小 
     * @param total 总记录数
     * @param list 当前页数据
     * @param <T> 数据类型
     * @return 分页结果对象
     */
    public static <T> PageResult<T> build(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        // 计算总页数
        int pages = (int) Math.ceil((double) total / pageSize);
        
        // 是否有上一页
        boolean hasPrevious = pageNum > 1;
        
        // 是否有下一页
        boolean hasNext = pageNum < pages;
        
        return new PageResult<>(pageNum, pageSize, total, pages, list, hasPrevious, hasNext);
    }
} 