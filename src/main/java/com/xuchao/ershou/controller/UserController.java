package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.ApiResponse;
import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dao.user.UserBanDao;
import com.xuchao.ershou.model.dao.user.UserChangePasswordDao;
import com.xuchao.ershou.model.dao.user.UserLoginDao;
import com.xuchao.ershou.model.dao.user.UserRegisterDao;
import com.xuchao.ershou.model.dao.user.UserAdminDao;
import com.xuchao.ershou.model.dao.user.UserAddressDao;
import com.xuchao.ershou.model.dao.user.UserResetPasswordDao;
import com.xuchao.ershou.model.dao.user.UserRoleUpdateDao;
import com.xuchao.ershou.model.dao.user.UserUnbanDao;
import com.xuchao.ershou.model.dao.user.UserUpdateDao;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.model.entity.UserAddress;
import com.xuchao.ershou.model.vo.UserRoleVO;
import com.xuchao.ershou.service.UserService;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/user/register")
    public Object register(@RequestBody @Valid UserRegisterDao registerDao) {
        // 检查用户名是否已存在
        if (userService.checkUsernameExists(registerDao.getUsername())) {
            throw new BusinessException(ErrorCode.REGISTER_USER_EXIST);
        }
        
        User user = new User()
            .setUsername(registerDao.getUsername())
            .setPassword(registerDao.getPassword())
            .setPhone(registerDao.getPhone())
            .setEmail(registerDao.getEmail())
            .setRole("普通用户")
            .setBalance(new BigDecimal("0"))
            .setIsLocked(false)
            .setCreateTime(LocalDateTime.now());
        userService.insertUser(user);
        return ResultUtils.success("注册成功");
    }

    @PostMapping("/user/login")
    public Object login(@RequestBody @Valid UserLoginDao loginDao) {
        User user = userService.selectUserByUsernameAndPassword(loginDao);
        if (user == null) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername());
        return ResultUtils.success(token);
    }

    /**
     * 管理员登录接口
     * @param adminDao 管理员登录信息
     * @return token和登录成功的提示
     */
    @PostMapping("/user/admin")
    public BaseResponse<Map<String, Object>> adminLogin(@RequestBody @Valid UserAdminDao adminDao) {
        User admin = userService.selectAdminByUsernameAndPassword(adminDao);
        if (admin == null) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }
        
        // 生成token
        String token = jwtUtil.generateToken(admin.getUserId(), admin.getUsername());
        
        // 构造返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("message", "管理员登录成功");
        
        return ResultUtils.success(result);
    }

    @PostMapping("/user/address")
    public Object addAddress(@RequestBody @Valid UserAddress address) {
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        address.setUserId(currentUserId);
        userService.insertUserAddress(address);
        return ResultUtils.success("地址添加成功");
    }
    
    /**
     * 获取当前登录用户信息
     * @return 用户信息（不包括敏感字段）
     */
    @GetMapping("/user/info")
    public BaseResponse<User> getUserInfo() {
        // 从当前请求中获取用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 查询用户信息
        User userInfo = userService.getUserInfo(currentUserId);
        if (userInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        
        return ResultUtils.success(userInfo);
    }
    
    /**
     * 修改当前登录用户信息
     * @param updateDao 要修改的用户信息
     * @return 修改后的用户信息
     */
    @PutMapping("/user/info")
    public BaseResponse<User> updateUserInfo(@RequestBody @Valid UserUpdateDao updateDao) {
        // 从当前请求中获取用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 更新并获取最新的用户信息
        User updatedUser = userService.updateUserInfo(currentUserId, updateDao);
        
        return ResultUtils.success(updatedUser);
    }
    
    /**
     * 修改当前登录用户密码
     * @param passwordDao 包含旧密码和新密码的请求体
     * @return 修改结果
     */
    @PutMapping("/user/password")
    public BaseResponse<String> changePassword(@RequestBody @Valid UserChangePasswordDao passwordDao) {
        // 从当前请求中获取用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 修改密码
        boolean success = userService.changePassword(currentUserId, passwordDao);
        
        if (success) {
            return ResultUtils.success("密码修改成功");
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "密码修改失败");
        }
    }
    
    /**
     * 获取当前登录用户角色信息
     * @return 角色信息
     */
    @GetMapping("/user/role")
    public BaseResponse<UserRoleVO> getUserRole() {
        // 从当前请求中获取用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 查询用户角色信息
        UserRoleVO userRoleVO = userService.getUserRole(currentUserId);
        
        return ResultUtils.success(userRoleVO);
    }
    
    /**
     * 修改用户角色（仅管理员可用）
     * @param roleUpdateDao 角色更新请求
     * @return 处理结果
     */
    @PutMapping("/admin/user/role")
    public BaseResponse<String> updateUserRole(@RequestBody @Valid UserRoleUpdateDao roleUpdateDao) {
        // 从当前请求中获取用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 调用服务层方法修改用户角色
        boolean success = userService.updateUserRole(currentUserId, roleUpdateDao);
        
        if (success) {
            return ResultUtils.success("用户角色修改成功");
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户角色修改失败");
        }
    }
    
    /**
     * 封禁用户（仅管理员可用）
     * @param userBanDao 封禁用户请求体
     * @return 处理结果
     */
    @PutMapping("/admin/user/ban")
    public BaseResponse<String> banUser(@RequestBody @Valid UserBanDao userBanDao) {
        // 从当前请求中获取用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 调用服务层方法封禁用户
        boolean success = userService.banUser(currentUserId, userBanDao);
        
        if (success) {
            return ResultUtils.success("用户封禁成功");
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户封禁失败");
        }
    }
    
    /**
     * 解封用户（仅管理员可用）
     * @param userUnbanDao 解封用户请求体
     * @return 处理结果
     */
    @PutMapping("/admin/user/unban")
    public BaseResponse<String> unbanUser(@RequestBody @Valid UserUnbanDao userUnbanDao) {
        // 从当前请求中获取用户ID
        Long currentUserId = CurrentUserUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 调用服务层方法解封用户
        boolean success = userService.unbanUser(currentUserId, userUnbanDao);
        
        if (success) {
            return ResultUtils.success("用户解封成功");
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户解封失败");
        }
    }

    /**
     * 通过验证码重置密码
     */
    @PostMapping("/user/resetPassword")
    public ApiResponse<Boolean> resetPassword(@RequestBody @Valid UserResetPasswordDao resetPasswordDao) {
        boolean result = userService.resetPassword(resetPasswordDao);
        if (result) {
            return ApiResponse.success("密码重置成功", true);
        } else {
            return ApiResponse.error(500, "密码重置失败");
        }
    }
}