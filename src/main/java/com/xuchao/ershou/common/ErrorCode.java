package com.xuchao.ershou.common;

public enum ErrorCode {
    SUCCESS(0, "成功"),
    PARAMS_ERROR(40000, "请求参数错误"),
    LOGIN_PARAMS_ERROR(40001, "用户名或密码错误"),
    REGISTER_USER_EXIST(40002, "用户已存在"),
    LOGIN_FAILED(40003, "用户不存在"),
    UNAUTHORIZED(40100, "未授权"),
    FORBIDDEN(40300, "无权限"),
    NOT_FOUND(40400, "请求数据不存在"),
    SYSTEM_ERROR(50000, "系统内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}