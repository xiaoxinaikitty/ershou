package com.xuchao.ershou.model.vo;

import lombok.Data;
import java.util.Date;

/**
 * 用户分页列表项VO
 */
@Data
public class UserPageVO {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 手机号(脱敏)
     */
    private String phone;
    
    /**
     * 邮箱(脱敏)
     */
    private String email;
    
    /**
     * 性别(0-未知 1-男 2-女)
     */
    private Integer gender;
    
    /**
     * 状态(0-禁用 1-正常)
     */
    private Integer status;
    
    /**
     * 状态文本
     */
    private String statusText;
    
    /**
     * 注册时间
     */
    private Date registerTime;
    
    /**
     * 最后登录时间
     */
    private Date lastLoginTime;
    
    /**
     * 用户角色
     */
    private String role;
    
    /**
     * 发布商品数量
     */
    private Integer productCount;
    
    /**
     * 交易订单数量
     */
    private Integer orderCount;
} 