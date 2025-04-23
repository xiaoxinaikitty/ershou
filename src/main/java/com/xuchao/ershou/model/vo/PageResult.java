package com.xuchao.ershou.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 分页结果VO
 */
@Data
public class PageResult<T> {

    /**
     * 当前页码
     */
    private long pageNum;

    /**
     * 每页记录数
     */
    private long pageSize;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 总页数
     */
    private long pages;

    /**
     * 当前页数据
     */
    private List<T> list;

    /**
     * 是否有上一页
     */
    private boolean hasPrevious;

    /**
     * 是否有下一页
     */
    private boolean hasNext;

    /**
     * 根据分页参数和数据列表构造分页结果
     * 
     * @param pageNum 当前页码
     * @param pageSize 每页记录数
     * @param total 总记录数
     * @param list 当前页数据
     */
    public PageResult(long pageNum, long pageSize, long total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
        this.pages = (total + pageSize - 1) / pageSize;
        this.hasPrevious = pageNum > 1;
        this.hasNext = pageNum < this.pages;
    }
} 