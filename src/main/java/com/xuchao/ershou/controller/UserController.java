package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dao.user.UserChangePasswordDao;
import com.xuchao.ershou.model.dao.user.UserLoginDao;
import com.xuchao.ershou.model.dao.user.UserRegisterDao;
import com.xuchao.ershou.model.dao.user.UserAdminDao;
import com.xuchao.ershou.model.dao.user.UserAddressDao;
import com.xuchao.ershou.model.dao.user.UserUpdateDao;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.model.entity.UserAddress;
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
            .setPassword(registerDao.getPassword());
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

    @PostMapping("/user/admin")
    public Object adminLogin(@RequestBody @Valid UserAdminDao adminDao) {
        User admin = userService.selectAdminByUsernameAndPassword(adminDao);
        if (admin == null) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }
        return ResultUtils.success(admin);
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
}