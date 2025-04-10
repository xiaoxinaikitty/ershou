package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.CurrentUserUtils;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dao.user.UserLoginDao;
import com.xuchao.ershou.model.dao.user.UserRegisterDao;
import com.xuchao.ershou.model.dao.user.UserAdminDao;
import com.xuchao.ershou.model.dao.user.UserAddressDao;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.model.entity.UserAddress;
import com.xuchao.ershou.service.UserService;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
}