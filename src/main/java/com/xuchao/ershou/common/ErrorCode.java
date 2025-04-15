package com.xuchao.ershou.common;

/**
 * 错误码
 */
public enum ErrorCode {
    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    LOGIN_PARAMS_ERROR(40001, "用户名或密码错误"),
    REGISTER_USER_EXIST(40002, "用户已存在"),
    LOGIN_FAILED(40003, "用户不存在"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),
    UNAUTHORIZED(40100, "未授权"),
    NOT_FOUND(40400, "数据不存在");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
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