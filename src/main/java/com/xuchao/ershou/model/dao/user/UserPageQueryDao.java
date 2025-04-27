package com.xuchao.ershou.model.dao.user;

import lombok.Data;

/**
 * 用户分页查询请求对象
 */
@Data
public class UserPageQueryDao {
    
    /**
     * 当前页码，从1开始
     */
    private Integer pageNum = 1;
    
    /**
     * 每页数量，默认10
     */
    private Integer pageSize = 10;
    
    /**
     * 用户名/昵称/手机号关键词
     */
    private String keyword;
    
    /**
     * 用户状态筛选(0-禁用 1-正常)
     */
    private Integer status;
    
    /**
     * 注册开始时间
     */
    private String startTime;
    
    /**
     * 注册结束时间
     */
    private String endTime;
    
    /**
     * 排序字段 (register-注册时间, login-最后登录时间)
     */
    private String sortField = "register";
    
    /**
     * 排序方式 (asc-升序, desc-降序)
     */
    private String sortOrder = "desc";
} 