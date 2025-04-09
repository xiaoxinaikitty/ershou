package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.model.dao.user.UserRegisterDao;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.service.UserService;
import com.xuchao.ershou.common.ErrorCode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

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
}