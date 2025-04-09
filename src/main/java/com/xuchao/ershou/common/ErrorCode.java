package com.xuchao.ershou.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    PARAMS_ERROR(40000, "请求参数错误", "客户端请求参数校验不通过"),
    REGISTER_USER_EXIST(40002, "用户已存在", "用户已存在"),
    LOGIN_FAILED(40003, "用户名或密码错误", "用户名或密码错误"),

    SYSTEM_ERROR(50000, "系统内部异常", "服务器运行时异常");


    private final int code;
    private final String message;
    private final String description;
}