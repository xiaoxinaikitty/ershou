package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dao.user.UserPageQueryDao;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.model.vo.PageResult;
import com.xuchao.ershou.model.vo.UserPageVO;
import com.xuchao.ershou.service.UserService;
import com.xuchao.ershou.common.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 分页获取用户列表
     * @param queryParams 查询参数
     * @return 分页结果
     */
    @GetMapping("/user/list")
    public BaseResponse<PageResult<UserPageVO>> listUsers(UserPageQueryDao queryParams) {
        // 获取当前登录用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 检查当前用户是否是管理员
        User currentUser = userService.selectUserById(currentUserId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "当前用户不存在");
        }
        
        if (!"系统管理员".equals(currentUser.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "权限不足，仅系统管理员可访问此接口");
        }
        
        // 调用服务层分页查询用户
        PageResult<UserPageVO> pageResult = userService.pageUsers(queryParams);
        
        return ResultUtils.success(pageResult);
    }
    
    /**
     * 获取用户数量统计
     * @return 用户数量统计信息
     */
    @GetMapping("/user/count")
    public BaseResponse<Map<String, Long>> getUserCount() {
        // 获取当前登录用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 检查当前用户是否是管理员
        User currentUser = userService.selectUserById(currentUserId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "当前用户不存在");
        }
        
        if (!"系统管理员".equals(currentUser.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "权限不足，仅系统管理员可访问此接口");
        }
        
        // 创建统计数据
        Map<String, Long> countMap = new HashMap<>();
        countMap.put("total", userService.countAllUsers());
        countMap.put("normalCount", userService.countUsersByLockState(false));
        countMap.put("lockedCount", userService.countUsersByLockState(true));
        countMap.put("adminCount", userService.countUsersByRole("系统管理员"));
        
        return ResultUtils.success(countMap);
    }
} 